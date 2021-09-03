package com.marketlogic.app.project.service.impl;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.common.error.ErrorCode;
import com.marketlogic.app.project.dto.SectionDTO;
import com.marketlogic.app.project.dto.SectionResponse;
import com.marketlogic.app.project.entity.Section;
import com.marketlogic.app.project.repository.ProjectRepository;
import com.marketlogic.app.project.repository.SectionRepository;
import com.marketlogic.app.project.service.SectionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class SectionServiceImpl implements SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SectionResponse findAll(long projectId, Pageable pageable) {
        var pageableSection = sectionRepository.findAllByProjectId(projectId, pageable);
        var sectionResponse = new SectionResponse();
        if (pageableSection != null
                && !pageableSection.getContent().isEmpty()) {
            sectionResponse.setContent(pageableSection.getContent().stream()
                    .map(section -> modelMapper.map(section, SectionDTO.class))
                    .collect(Collectors.toList()));
            sectionResponse.setTotalElements(pageableSection.getTotalElements());
            sectionResponse.setTotalPages(pageableSection.getTotalPages());
            sectionResponse.setNumberOfElements(pageableSection.getNumberOfElements());
            sectionResponse.setSize(pageableSection.getSize());
            log.debug("Response prepared successfully!");
        }
        return sectionResponse;
    }

    @Override
    public SectionDTO findByProjectIdAndId(long projectId, long sectionId) {
        return Optional.ofNullable(sectionRepository.findByProjectIdAndId(projectId, sectionId))
                .map(section -> modelMapper.map(section, SectionDTO.class))
                .orElseThrow(() -> new AppServiceException(ErrorCode.SECTION_NOT_FOUND));
    }

    @Override
    public SectionDTO save(long projectId, SectionDTO sectionDTO) {
        var project = projectRepository.findById(projectId);
        if (project == null)
            throw new AppServiceException(ErrorCode.PROJECT_NOT_FOUND);
        var section = modelMapper.map(sectionDTO, Section.class);
        section.setProject(project);
        project.getSections().add(section);
        return modelMapper.map(sectionRepository.save(section), SectionDTO.class);
    }

    @Override
    public SectionDTO update(long projectId, long sectionId, SectionDTO sectionDTO) {
        var section = sectionRepository.findByProjectIdAndId(projectId, sectionId);
        if (section == null)
            throw new AppServiceException(ErrorCode.SECTION_NOT_FOUND);
        if (StringUtils.isNotBlank(sectionDTO.getTitle()))
            section.setTitle(sectionDTO.getTitle());
        if (StringUtils.isNotBlank(sectionDTO.getDescription()))
            section.setDescription(sectionDTO.getDescription());
        return modelMapper.map(sectionRepository.save(section), SectionDTO.class);
    }

    @Override
    public void deleteSectionById(long projectId, long sectionId) {
        var section = sectionRepository.findByProjectIdAndId(projectId, sectionId);
        if (section == null)
            throw new AppServiceException(ErrorCode.SECTION_NOT_FOUND);
        sectionRepository.delete(section);
    }
}
