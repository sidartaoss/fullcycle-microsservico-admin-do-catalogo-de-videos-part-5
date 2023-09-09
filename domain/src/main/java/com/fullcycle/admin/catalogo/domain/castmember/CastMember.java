package com.fullcycle.admin.catalogo.domain.castmember;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.time.Instant;
import java.util.Objects;

public class CastMember extends AggregateRoot<CastMemberID> {

    private String name;
    private CastMemberType type;
    private Instant createdAt;
    private Instant updatedAt;

    private CastMember(
            final CastMemberID anId,
            final String aName,
            final CastMemberType aType,
            final Instant aCreatedAt,
            final Instant anUpdatedAt) {
        super(anId);
        this.name = aName;
        this.type = aType;
        this.createdAt = Objects.requireNonNull(aCreatedAt);
        this.updatedAt = Objects.requireNonNull(anUpdatedAt);
        selfValidate();
    }

    public static CastMember newCastMember(final String aName, final CastMemberType aType) {
        final var aNow = InstantUtils.now();
        final var anId = CastMemberID.unique();
        return new CastMember(anId, aName, aType, aNow, aNow);
    }

    public static CastMember with(
            final CastMemberID anId,
            final String aName,
            final CastMemberType aType,
            final Instant aCreatedAt,
            final Instant anUpdatedAt
    ) {
        return new CastMember(anId, aName, aType, aCreatedAt, anUpdatedAt);
    }

    public static CastMember with(final CastMember aCastMember) {
        final var anId = aCastMember.getId();
        final var aName = aCastMember.getName();
        final var aType = aCastMember.getType();
        final var aCreatedAt = aCastMember.getCreatedAt();
        final var anUpdatedAt = aCastMember.getUpdatedAt();
        return new CastMember(anId, aName, aType, aCreatedAt, anUpdatedAt);
    }

    public CastMember update(final String aName, final CastMemberType aType) {
        this.name = aName;
        this.type = aType;
        this.updatedAt = InstantUtils.now();
        selfValidate();
        return this;
    }

    @Override
    public void validate(final ValidationHandler aHandler) {
        new CastMemberValidator(this, aHandler).validate();
    }

    public String getName() {
        return name;
    }

    public CastMemberType getType() {
        return type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    private void selfValidate() {
        final var notification = Notification.create();
        validate(notification);
        if (notification.hasErrors()) {
            throw new NotificationException("Failed to create Aggregate CastMember", notification);
        }
    }
}
