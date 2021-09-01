package com.marketlogic.app.project.service;

import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.dto.ProjectResponse;
import com.marketlogic.app.project.entity.Project;
import org.springframework.data.domain.Page;

public interface ProjectService {
    ProjectResponse findAll(int page, int size);

    ProjectDTO findById(long projectId);

    ProjectDTO save(ProjectDTO projectDTO);

    ProjectDTO update(long projectId, ProjectDTO projectDTO);

    void deleteById(long projectId);

    void publishById(long projectId);
}
