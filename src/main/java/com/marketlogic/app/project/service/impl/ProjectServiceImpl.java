package com.marketlogic.app.project.service.impl;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.common.error.ErrorCode;
import com.marketlogic.app.project.constants.Status;
import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.dto.ProjectResponse;
import com.marketlogic.app.project.entity.Project;
import com.marketlogic.app.project.entity.ProjectRecord;
import com.marketlogic.app.project.entity.Section;
import com.marketlogic.app.project.repository.ProjectRecordRepository;
import com.marketlogic.app.project.repository.ProjectRepository;
import com.marketlogic.app.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectRecordRepository projectRecordRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topics.PublishProject}")
    private String topicPublishProject;

    @Override
    public ProjectResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size
        );
        var pageableProject = projectRepository.findAll(pageable);
        log.debug("Retrieved project successfully!");
        var projectResponse = new ProjectResponse();
        if (pageableProject != null
                && !pageableProject.getContent().isEmpty()) {
            projectResponse.setContent(pageableProject.getContent().stream()
                    .map(project -> modelMapper.map(project, ProjectDTO.class))
                    .collect(Collectors.toList()));
            projectResponse.setTotalElements(pageableProject.getTotalElements());
            projectResponse.setTotalPages(pageableProject.getTotalPages());
            projectResponse.setNumberOfElements(pageableProject.getNumberOfElements());
            projectResponse.setSize(pageableProject.getSize());
            log.debug("Response prepared successfully!");
        }
        return projectResponse;

    }

    @Override
    public ProjectDTO findById(long projectId) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        return modelMapper.map(project, ProjectDTO.class);
    }

    private void validateProject(Project project) {
        if (project == null) {
            log.error("Project not found");
            throw new AppServiceException(ErrorCode.PROJECT_NOT_FOUND);
        }
    }

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        if (projectDTO.getId() != null) {
            var project = projectRepository.findById(projectDTO.getId());
            if (project != null) {
                log.error("Project exists with the ID. Please try update");
                throw new AppServiceException(ErrorCode.CONFLICTS);
            }
        }
        var projectEntity = modelMapper.map(projectDTO, Project.class);
        projectEntity.setStatus(Status.DRAFT);
        if (projectEntity.getSections() != null && !projectEntity.getSections().isEmpty())
            projectEntity.getSections().forEach(section -> section.setProject(projectEntity));
        return modelMapper.map(projectRepository.save(projectEntity), ProjectDTO.class);
    }

    @Override
    public ProjectDTO update(long projectId, ProjectDTO projectDTO) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        if (project.getStatus().equals(Status.PUBLISHED)) {
            log.error("Can't update published project");
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
        updateProjectDetails(projectDTO, project);
        updateSectionDetails(projectDTO, project);
        return modelMapper.map(projectRepository.save(project), ProjectDTO.class);
    }

    private void updateSectionDetails(ProjectDTO projectDTO, Project project) {
        if (projectDTO.getSections() != null) {
            if (projectDTO.getSections().isEmpty()) {
                project.getSections().clear();
            } else {
                var sections = prepareSectionDetails(projectDTO, project);
                project.getSections().clear();
                project.getSections().addAll(sections);
            }
        }
    }

    private Set<Section> prepareSectionDetails(ProjectDTO projectDTO, Project project) {
        return projectDTO.getSections().stream().map(sectionDTO -> {
            var section = new Section();
            if (sectionDTO.getId() != null)
                section = project.getSections().stream()
                        .filter(sec -> sec.getId() == sectionDTO.getId())
                        .findFirst().orElse(new Section());
            if (sectionDTO.getId() != null)
                section.setId(sectionDTO.getId());
            section.setTitle(sectionDTO.getTitle());
            section.setDescription(sectionDTO.getDescription());
            section.setProject(project);
            return section;
        }).collect(Collectors.toSet());
    }

    private void updateProjectDetails(ProjectDTO projectDTO, Project project) {
        if (StringUtils.isNotBlank(projectDTO.getTitle()))
            project.setTitle(projectDTO.getTitle());
        if (StringUtils.isNotBlank(projectDTO.getDescription()))
            project.setDescription(projectDTO.getDescription());
        if (projectDTO.getType() != null)
            project.setType(projectDTO.getType());
    }

    @Override
    public void deleteById(long projectId) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        if (project.getStatus().equals(Status.PUBLISHED)) {
            log.error("Published project can't deleted");
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
        projectRepository.delete(project);
    }

    @Override
    public void publishById(long projectId) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        if (project.getStatus().equals(Status.PUBLISHED)) {
            log.error("Project published already");
            throw new AppServiceException(ErrorCode.CONFLICTS);
        } else {
            project.setStatus(Status.PUBLISHED);
            project = projectRepository.save(project);
            prepareAndSaveProjectRecord(project);
            sendProjectRecordToSearchService(project);
        }
    }

    private void prepareAndSaveProjectRecord(Project project) {
        var projectRecord = modelMapper.map(project, ProjectRecord.class);
        if (projectRecord != null) {
            if (projectRecord.getSectionRecords() != null && !projectRecord.getSectionRecords().isEmpty())
                projectRecord.getSectionRecords().forEach(section -> section.setProjectRecord(projectRecord));
            projectRecord.setProject(project);
            projectRecordRepository.save(projectRecord);
            log.info("ProjectRecord saved successfully");
        }
    }

    private void sendProjectRecordToSearchService(Project project) {
        try {
            log.info("Notifying project publish to consumers through Kafka -> {}", project.getTitle());
            var content = prepareContent(project);
            this.kafkaTemplate.send(topicPublishProject,
                    String.format("{\"id\":%s, \"content\":\"%s\"}", project.getId(), content));
            log.info("Notified Successfully");
        } catch (Exception e) {
            log.error("Exception while notifying machine config changes {} through Kafka: ", project.getTitle(), e);
        }
    }

    private StringBuilder prepareContent(Project project) {
        var content = new StringBuilder(project.getTitle())
                .append(" ")
                .append(project.getDescription())
                .append(" ");
        if (project.getSections() != null && !project.getSections().isEmpty()) {
            content.append(project.getSections().stream()
                    .map(section -> section.getTitle() + " " + section.getDescription())
                    .collect(Collectors.joining(" ")));
        }
        return content;
    }
}
