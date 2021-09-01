package com.marketlogic.app.project.utils;

import com.marketlogic.app.project.constants.Status;
import org.springframework.context.annotation.Configuration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
@Configuration
public class StatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status status) {
        return status.getName();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return Status.parse(dbData);
    }

}