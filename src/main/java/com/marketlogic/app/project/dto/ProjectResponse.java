package com.marketlogic.app.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProjectResponse {
    List<ProjectDTO> content = new ArrayList<>();
    long totalPages;
    long totalElements;
    long numberOfElements;
    long size;
}
