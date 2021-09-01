package com.marketlogic.app.project.utils;

import com.marketlogic.app.project.constants.Type;
import org.springframework.context.annotation.Configuration;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
@Configuration
public class TypeConverter implements AttributeConverter<Type, String> {

    @Override
    public String convertToDatabaseColumn(Type type) {
        return type.getName();
    }

    @Override
    public Type convertToEntityAttribute(String type) {
        return Type.parse(type);
    }

}