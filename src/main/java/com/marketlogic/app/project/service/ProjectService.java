package com.marketlogic.app.project.service;

import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.dto.ProjectResponse;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponse findAll(Pageable pageable);

    ProjectDTO findById(long projectId);

    ProjectDTO save(ProjectDTO projectDTO);

    ProjectDTO update(long projectId, ProjectDTO projectDTO);

    void deleteById(long projectId);

    void publishById(long projectId);
}
