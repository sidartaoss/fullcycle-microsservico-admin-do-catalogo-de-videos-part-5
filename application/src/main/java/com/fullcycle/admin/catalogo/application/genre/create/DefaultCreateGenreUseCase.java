package com.fullcycle.admin.catalogo.application.genre.create;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;

import java.util.List;
import java.util.Objects;

import static com.fullcycle.admin.catalogo.application.utils.ValidationUtils.validateAggregate;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultCreateGenreUseCase(
            final CategoryGateway categoryGateway,
            final GenreGateway genreGateway) {
        Objects.requireNonNull(categoryGateway);
        Objects.requireNonNull(genreGateway);
        this.categoryGateway = categoryGateway;
        this.genreGateway = genreGateway;
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand aCommand) {
        final var aName = aCommand.name();
        final var aCategories = toCategoryIDs(aCommand.categories());

        final var aNotification = Notification.create();
        final var aCategoriesValidation = validateCategories(aCategories);
        aNotification.append(aCategoriesValidation);

        final var aGenre = aNotification.validate(() -> Genre.newGenre(aName));
        if (aNotification.hasErrors()) {
            throw new NotificationException("Could not create Aggregate Genre", aNotification);
        }

        aGenre.addCategories(aCategories);

        return CreateGenreOutput
                .from(this.genreGateway.create(aGenre));
    }

    private ValidationHandler validateCategories(final List<CategoryID> ids) {
        return validateAggregate("categories", ids, categoryGateway::existsByIds);
    }

    private List<CategoryID> toCategoryIDs(final List<String> aCategories) {
        return aCategories.stream()
                .map(CategoryID::from)
                .toList();
    }
}
