package com.fullcycle.admin.catalogo.application.castmember.retrieve.get;

import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;

import java.time.Instant;

public record GetCastMemberByIdOutput(
        String id,
        String name,
        CastMemberType type,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetCastMemberByIdOutput from(final CastMember aCastMember) {
        final var anId = aCastMember.getId().getValue();
        return new GetCastMemberByIdOutput(
                anId,
                aCastMember.getName(),
                aCastMember.getType(),
                aCastMember.getCreatedAt(),
                aCastMember.getUpdatedAt());
    }
}
