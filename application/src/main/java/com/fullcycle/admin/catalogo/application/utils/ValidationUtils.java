package com.fullcycle.admin.catalogo.application.utils;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static <T extends Identifier> ValidationHandler validateAggregate(
            final String aggregate,
            final Collection<T> ids,
            final Function<Iterable<T>, List<T>> existsByIds) {
        final var aNotification = Notification.create();
        if (ids.isEmpty()) {
            return aNotification;
        }
        final var retrievedIds = existsByIds.apply(ids);
        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);
            final var missingIdsMessage = missingIds.stream()
                    .map(Identifier::getValue)
                    .collect(Collectors.joining(", "));
            aNotification.append(new Error("Some %s could not be found: %s"
                    .formatted(aggregate, missingIdsMessage)));
        }
        return aNotification;
    }
}
