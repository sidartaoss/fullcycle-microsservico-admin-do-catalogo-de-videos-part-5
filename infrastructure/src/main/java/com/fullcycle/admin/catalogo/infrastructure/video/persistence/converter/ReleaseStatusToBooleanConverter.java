package com.fullcycle.admin.catalogo.infrastructure.video.persistence.converter;

import com.fullcycle.admin.catalogo.domain.video.ReleaseStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;

@Convert
public class ReleaseStatusToBooleanConverter implements AttributeConverter<ReleaseStatus, Boolean> {

    @Override
    public Boolean convertToDatabaseColumn(final ReleaseStatus attribute) {
        return ReleaseStatus.RELEASED.equals(attribute) ? Boolean.TRUE : Boolean.FALSE;
    }

    @Override
    public ReleaseStatus convertToEntityAttribute(final Boolean dbData) {
        return Boolean.TRUE.equals(dbData) ? ReleaseStatus.RELEASED : ReleaseStatus.NOT_RELEASED;
    }
}
