package com.fullcycle.admin.catalogo.application.genre.update;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultUpdateGenreUseCase(
            final CategoryGateway categoryGateway,
            final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public UpdateGenreOutput execute(final UpdateGenreCommand aCommand) {
        final var anId = GenreID.from(aCommand.id());
        final var aName = aCommand.name();
        final var aCategories = toCategoryIDs(aCommand.categories());
        final var aGenre = this.genreGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var aNotification = Notification.create();
        final var aCategoriesValidation = validateCategories(aCategories);
        aNotification.append(aCategoriesValidation);

        aNotification.validate(() -> aGenre.update(aName, aCategories));

        if (aNotification.hasErrors()) {
            throw new NotificationException("Could not update Aggregate Genre %s"
                    .formatted(aCommand.id()), aNotification);
        }
        return UpdateGenreOutput.from(
                this.genreGateway.update(aGenre));
    }

    private ValidationHandler validateCategories(final List<CategoryID> ids) {
        final var aNotification = Notification.create();
        if (ids.isEmpty()) {
            return aNotification;
        }
        final var retrievedIds = categoryGateway.existsByIds(ids);
        if (ids.size() != retrievedIds.size()) {
            final var missingIds = new ArrayList<>(ids);
            missingIds.removeAll(retrievedIds);
            final var missingIdsMessage = missingIds.stream()
                    .map(CategoryID::getValue)
                    .collect(Collectors.joining(", "));
            aNotification.append(new Error("Some categories could not be found: %s"
                    .formatted(missingIdsMessage)));
        }
        return aNotification;
    }

    private List<CategoryID> toCategoryIDs(final List<String> categories) {
        return categories.stream()
                .map(CategoryID::from)
                .toList();
    }

    private Supplier<NotFoundException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Genre.class, anId);
    }
}
