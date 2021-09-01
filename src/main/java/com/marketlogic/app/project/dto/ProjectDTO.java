package com.marketlogic.app.project.dto;

import com.marketlogic.app.project.constants.Status;
import com.marketlogic.app.project.constants.Type;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDTO {
    private Long id;
    private String title;
    private String description;
    private Type type;
    private Status status;
    private List<SectionDTO> sections;
}
