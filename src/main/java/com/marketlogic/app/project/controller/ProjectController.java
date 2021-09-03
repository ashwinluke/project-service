package com.marketlogic.app.project.controller;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.common.error.ErrorCode;
import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.dto.ProjectResponse;
import com.marketlogic.app.project.service.ProjectService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/project", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Endpoints are used to create/update/delete the projects")
@Slf4j
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @GetMapping
    @ApiOperation(value = "To get the all projects", response = ProjectResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved all projects"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ProjectResponse getAllProjects(@RequestParam(defaultValue = "1") @ApiParam int page,
                                          @RequestParam(defaultValue = "25") @ApiParam int size) {
        return projectService.findAll(page, size);
    }

    @GetMapping("/{projectId}")
    @ApiOperation(value = "To get a project by Id", response = ProjectDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved project"),
            @ApiResponse(code = 400, message = "Invalid project id"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable long projectId) {
        validateProjectId(projectId <= 0);
        return Optional.ofNullable(projectService.findById(projectId)).map(ResponseEntity::ok)
                .orElseThrow(() -> {
                    log.error("Project not found");
                    throw new AppServiceException(ErrorCode.PROJECT_NOT_FOUND);
                });
    }

    @PostMapping
    @ApiOperation(value = "To create a project", response = ProjectDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully created a project"),
            @ApiResponse(code = 400, message = "Invalid project details"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        if (projectDTO == null || StringUtils.isBlank(projectDTO.getTitle())) {
            log.error("Invalid Project details");
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.ok(projectService.save(projectDTO));
    }

    @PutMapping("/{projectId}")
    @ApiOperation(value = "To update the project by Id", response = ProjectDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully updated the project"),
            @ApiResponse(code = 400, message = "Invalid project details"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable long projectId,
                                                    @RequestBody ProjectDTO projectDTO) {
        validateProjectId(projectId <= 0 || projectDTO == null);
        return ResponseEntity.ok(projectService.update(projectId, projectDTO));
    }

    @DeleteMapping("/{projectId}")
    @ApiOperation(value = "To delete the project by Id", response = HttpStatus.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully deleted the project"),
            @ApiResponse(code = 400, message = "Invalid project details"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<HttpStatus> deleteById(@PathVariable long projectId) {
        validateProjectId(projectId <= 0);
        projectService.deleteById(projectId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{projectId}")
    @ApiOperation(value = "To publish the project by Id", response = HttpStatus.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully published the project"),
            @ApiResponse(code = 400, message = "Invalid project details"),
            @ApiResponse(code = 404, message = "Project not found"),
            @ApiResponse(code = 409, message = "Project already published"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<HttpStatus> publishById(@PathVariable long projectId) {
        validateProjectId(projectId <= 0);
        projectService.publishById(projectId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validateProjectId(boolean isInvalidProject) {
        if (isInvalidProject) {
            log.error("Invalid Project details");
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
    }
}
