package com.fullcycle.admin.catalogo.infrastructure.video.persistence.converter;

import com.fullcycle.admin.catalogo.domain.video.Rating;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class RatingConverter implements AttributeConverter<Rating, String> {

    @Override
    public String convertToDatabaseColumn(final Rating attribute) {
        return attribute != null ? attribute.getName() : null;
    }

    @Override
    public Rating convertToEntityAttribute(final String dbData) {
        return dbData != null ? Rating.of(dbData).orElse(null) : null;
    }
}
