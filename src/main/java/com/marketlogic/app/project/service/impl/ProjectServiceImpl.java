package com.marketlogic.app.project.service.impl;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.common.error.ErrorCode;
import com.marketlogic.app.project.constants.Status;
import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.dto.ProjectResponse;
import com.marketlogic.app.project.entity.Project;
import com.marketlogic.app.project.entity.ProjectRecord;
import com.marketlogic.app.project.repository.ProjectRecordRepository;
import com.marketlogic.app.project.repository.ProjectRepository;
import com.marketlogic.app.project.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var projectResponse = new ProjectResponse();
        projectResponse.setContent(pageableProject.getContent().stream()
                .map(project -> modelMapper.map(project, ProjectDTO.class))
                .collect(Collectors.toList()));
        projectResponse.setTotalElements(pageableProject.getTotalElements());
        projectResponse.setTotalPages(pageableProject.getTotalPages());
        projectResponse.setNumberOfElements(pageableProject.getNumberOfElements());
        projectResponse.setSize(pageableProject.getSize());
        return projectResponse;

    }

    @Override
    public ProjectDTO findById(long projectId) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        return modelMapper.map(project, ProjectDTO.class);
    }

    private void validateProject(Project project) {
        if (project == null)
            throw new AppServiceException(ErrorCode.PROJECT_NOT_FOUND);
    }

    @Override
    public ProjectDTO save(ProjectDTO projectDTO) {
        var projectEntity = modelMapper.map(projectDTO, Project.class);
        if (projectEntity != null) {
            if (projectEntity.getSections() != null && !projectEntity.getSections().isEmpty())
                projectEntity.getSections().forEach(section -> section.setProject(projectEntity));
            return modelMapper.map(projectRepository.save(projectEntity), ProjectDTO.class);
        } else {
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public ProjectDTO update(long projectId, ProjectDTO projectDTO) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        var projectEntity = modelMapper.map(projectDTO, Project.class);
        if (projectEntity != null && project.getStatus() != Status.PUBLISHED) {
            if (projectEntity.getSections() != null && !projectEntity.getSections().isEmpty())
                projectEntity.getSections().forEach(section -> section.setProject(projectEntity));
            return modelMapper.map(projectRepository.save(projectEntity), ProjectDTO.class);
        } else {
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public void deleteById(long projectId) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        projectRepository.delete(project);
    }

    @Override
    public void publishById(long projectId) {
        var project = projectRepository.findById(projectId);
        validateProject(project);
        if (project.getStatus().equals(Status.PUBLISHED)) {
            throw new AppServiceException(ErrorCode.CONFLICTS);
        } else {
            project.setStatus(Status.PUBLISHED);
            project = projectRepository.save(project);
            var projectRecord = modelMapper.map(project, ProjectRecord.class);
            if (projectRecord != null) {
                if (projectRecord.getSectionRecords() != null && !projectRecord.getSectionRecords().isEmpty())
                    projectRecord.getSectionRecords().forEach(section -> section.setProjectRecord(projectRecord));
                projectRecord.setProject(project);
                projectRecordRepository.save(projectRecord);
            }
            sendProjectRecordToSearchService(project);
        }
    }

    private void sendProjectRecordToSearchService(Project project) {
        try {
            log.debug("Notifying project publish to consumers through Kafka -> {}", project.getTitle());
            var content = new StringBuilder(project.getTitle())
                    .append(" ")
                    .append(project.getDescription())
                    .append(" ");
            if (project.getSections() != null && !project.getSections().isEmpty()) {
                content.append(project.getSections().stream()
                        .map(section -> section.getTitle() + " " + section.getDescription())
                        .collect(Collectors.joining(" ")));
            }
            this.kafkaTemplate.send(topicPublishProject,
                    String.format("{\"id\":%s, \"content\":\"%s\"}", project.getId(), content));
        } catch (Exception e) {
            log.error("Exception while notifying machine config changes {} through Kafka: ", project.getTitle(), e);
        }
    }
}
