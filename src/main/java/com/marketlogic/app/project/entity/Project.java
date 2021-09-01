package com.marketlogic.app.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.marketlogic.app.project.constants.Status;
import com.marketlogic.app.project.constants.Type;
import com.marketlogic.app.project.utils.StatusConverter;
import com.marketlogic.app.project.utils.TypeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    @Convert(converter = TypeConverter.class)
    private Type type;

    @Column
    @Convert(converter = StatusConverter.class)
    private Status status;

    @JsonIgnore
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();


}
