package com.marketlogic.app.config;

import com.marketlogic.app.project.entity.Project;
import com.marketlogic.app.project.entity.ProjectRecord;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {

        // Create your mapper
        var modelMapper = new ModelMapper();

        // Create a TypeMap for your mapping
        var typeMap =
                modelMapper.createTypeMap(Project.class, ProjectRecord.class);

        typeMap.addMappings(mapper -> mapper.map(Project::getSections,
                ProjectRecord::setSectionRecords));

        return modelMapper;
    }
}
