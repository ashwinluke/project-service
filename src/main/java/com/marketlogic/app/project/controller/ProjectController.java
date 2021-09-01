package com.marketlogic.app.project.controller;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.common.error.ErrorCode;
import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.entity.Project;
import com.marketlogic.app.project.service.ProjectService;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    public Page<Project> getAllProjects(@RequestParam(defaultValue = "1") @ApiParam int page,
                                        @RequestParam(defaultValue = "25") @ApiParam int size) {
        return projectService.findAll(page, size);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable long projectId) {
        if (projectId <= 0)
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        var project = projectService.findById(projectId);
        if (project == null) {
            throw new AppServiceException(ErrorCode.PROJECT_NOT_FOUND);
        } else {
            return ResponseEntity.ok(project);
        }
    }

    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        if (projectDTO == null || StringUtils.isBlank(projectDTO.getTitle())) {
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.ok(projectService.save(projectDTO));
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable long projectId,
                                                    @RequestBody ProjectDTO projectDTO) {
        if (projectId <= 0 || projectDTO == null)
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        return ResponseEntity.ok(projectService.update(projectId, projectDTO));
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<HttpStatus> deleteById(@PathVariable long projectId) {
        projectService.deleteById(projectId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<HttpStatus> publishById(@PathVariable long projectId) {
        projectService.publishById(projectId);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
