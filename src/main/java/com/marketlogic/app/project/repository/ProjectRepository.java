package com.marketlogic.app.project.repository;

import com.marketlogic.app.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Integer> {
    Page<Project> findAll(Pageable pageable);

    Project findById(long id);

}
