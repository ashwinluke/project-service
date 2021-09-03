package com.marketlogic.app.project.controller;

import com.marketlogic.app.common.error.AppServiceException;
import com.marketlogic.app.common.error.ErrorCode;
import com.marketlogic.app.project.dto.ProjectDTO;
import com.marketlogic.app.project.dto.ProjectResponse;
import com.marketlogic.app.project.dto.SectionDTO;
import com.marketlogic.app.project.dto.SectionResponse;
import com.marketlogic.app.project.service.SectionService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/project/{projectId}/section", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(value = "Endpoints are used to create/update/delete the sections")
@Slf4j
public class SectionController {

    @Autowired
    private SectionService sectionService;

    @GetMapping
    @ApiOperation(value = "To get the all sections by Project Id", response = ProjectResponse.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved all sections"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public SectionResponse getAllSection(@PathVariable long projectId,
                                         @RequestParam(defaultValue = "1") @ApiParam int page,
                                         @RequestParam(defaultValue = "25") @ApiParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        return sectionService.findAll(projectId, pageable);
    }

    @GetMapping("/{sectionId}")
    @ApiOperation(value = "To get a Section by Id", response = ProjectDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully retrieved section"),
            @ApiResponse(code = 400, message = "Invalid project id"),
            @ApiResponse(code = 404, message = "section not found"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<SectionDTO> getSectionById(@PathVariable long projectId,
                                                     @PathVariable long sectionId) {
        validateMandatoryIds(projectId <= 0 || sectionId <= 0);
        var section = sectionService.findByProjectIdAndId(projectId, sectionId);
        if (section == null) {
            log.error("Section not found");
            throw new AppServiceException(ErrorCode.SECTION_NOT_FOUND);
        } else {
            return ResponseEntity.ok(section);
        }
    }

    @PostMapping
    @ApiOperation(value = "To add section to the project", response = ProjectDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully created a section"),
            @ApiResponse(code = 400, message = "Invalid section details"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<SectionDTO> addSectionToTheProject(@PathVariable long projectId,
                                                             @RequestBody SectionDTO sectionDTO) {
        if (sectionDTO == null) {
            log.error("Invalid Project details");
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.ok(sectionService.save(projectId, sectionDTO));
    }

    @PutMapping("/{sectionId}")
    @ApiOperation(value = "To update the section by Id", response = ProjectDTO.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully updated the section"),
            @ApiResponse(code = 400, message = "Invalid section details"),
            @ApiResponse(code = 404, message = "section not found"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<SectionDTO> updateSection(@PathVariable long projectId,
                                                    @PathVariable long sectionId,
                                                    @RequestBody SectionDTO sectionDTO) {
        validateMandatoryIds(projectId <= 0 || sectionDTO == null || sectionId <= 0);
        return ResponseEntity.ok(sectionService.update(projectId, sectionId, sectionDTO));
    }

    @DeleteMapping("/{sectionId}")
    @ApiOperation(value = "To delete the section by Id", response = HttpStatus.class)
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Successfully deleted the section"),
            @ApiResponse(code = 400, message = "Invalid section details"),
            @ApiResponse(code = 404, message = "section not found"),
            @ApiResponse(code = 500, message = "Please contact the owner")})
    public ResponseEntity<HttpStatus> deleteSectionById(@PathVariable long projectId,
                                                        @PathVariable long sectionId) {
        validateMandatoryIds(projectId <= 0 || sectionId <= 0);
        sectionService.deleteSectionById(projectId, sectionId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    private void validateMandatoryIds(boolean isInvalidSection) {
        if (isInvalidSection) {
            log.error("Invalid Section details");
            throw new AppServiceException(ErrorCode.BAD_REQUEST);
        }
    }
}
