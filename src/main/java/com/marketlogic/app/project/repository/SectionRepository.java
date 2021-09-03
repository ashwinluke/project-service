package com.marketlogic.app.project.repository;

import com.marketlogic.app.project.entity.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SectionRepository extends PagingAndSortingRepository<Section, Integer> {
    Page<Section> findAllByProjectId(long projectId, Pageable pageable);

    Section findByProjectIdAndId(long projectId, long id);

}
