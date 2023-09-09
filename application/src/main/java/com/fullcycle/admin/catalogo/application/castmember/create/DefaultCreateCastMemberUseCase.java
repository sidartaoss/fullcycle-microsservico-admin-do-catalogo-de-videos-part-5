package com.fullcycle.admin.catalogo.application.castmember.create;

import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.Objects;

public non-sealed class DefaultCreateCastMemberUseCase extends CreateCastMemberUseCase {

    private final CastMemberGateway castMemberGateway;

    public DefaultCreateCastMemberUseCase(final CastMemberGateway castMemberGateway) {
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
    }

    @Override
    public CreateCastMemberOutput execute(final CreateCastMemberCommand aComand) {
        final var aName = aComand.name();
        final var aType = aComand.type();
        final var notification = Notification.create();
        final var aCastMember = notification.validate(
                () -> CastMember.newCastMember(aName, aType));
        if (notification.hasErrors()) {
            notify(notification);
        }
        return CreateCastMemberOutput.from(
                this.castMemberGateway.create(aCastMember));
    }

    private void notify(final Notification notification) {
        throw new NotificationException("Could not create Aggregate CastMember", notification);
    }
}
