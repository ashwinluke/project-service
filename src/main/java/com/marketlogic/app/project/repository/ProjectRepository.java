package com.marketlogic.app.project.repository;

import com.marketlogic.app.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Integer> {
    @EntityGraph(
            type = EntityGraph.EntityGraphType.FETCH,
            attributePaths = {
                    "sections"
            }
    )
    Page<Project> findAll(Pageable pageable);

    Project findById(long id);

    Project save(Project project);

}
