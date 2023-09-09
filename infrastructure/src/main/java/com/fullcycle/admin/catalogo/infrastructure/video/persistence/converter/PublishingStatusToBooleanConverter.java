package com.fullcycle.admin.catalogo.infrastructure.video.persistence.converter;

import com.fullcycle.admin.catalogo.domain.video.PublishingStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PublishingStatusToBooleanConverter implements AttributeConverter<PublishingStatus, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(final PublishingStatus attribute) {
        return PublishingStatus.PUBLISHED.equals(attribute) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public PublishingStatus convertToEntityAttribute(final Boolean dbData) {
        return Boolean.TRUE.equals(dbData) ? PublishingStatus.PUBLISHED : PublishingStatus.NOT_PUBLISHED;
    }
}
