package com.marketlogic.app.project.service;

import com.marketlogic.app.project.dto.SectionDTO;
import com.marketlogic.app.project.dto.SectionResponse;
import org.springframework.data.domain.Pageable;

public interface SectionService {

    SectionResponse findAll(long projectId, Pageable pageable);

    SectionDTO findByProjectIdAndId(long projectId, long sectionId);

    SectionDTO save(long projectId, SectionDTO sectionDTO);

    SectionDTO update(long projectId, long sectionId, SectionDTO sectionDTO);

    void deleteSectionById(long projectId, long sectionId);
}
