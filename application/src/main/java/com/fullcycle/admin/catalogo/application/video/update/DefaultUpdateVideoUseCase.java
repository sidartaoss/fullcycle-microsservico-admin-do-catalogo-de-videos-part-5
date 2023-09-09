package com.fullcycle.admin.catalogo.application.video.update;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.domain.video.*;

import java.time.Year;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.fullcycle.admin.catalogo.application.utils.ValidationUtils.validateAggregate;

public class DefaultUpdateVideoUseCase extends UpdateVideoUseCase {

    private final VideoGateway videoGateway;
    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;
    private final CastMemberGateway castMemberGateway;
    private final MediaResourceGateway mediaResourceGateway;

    public DefaultUpdateVideoUseCase(
            final VideoGateway videoGateway,
            final CategoryGateway categoryGateway,
            final GenreGateway genreGateway,
            final CastMemberGateway castMemberGateway,
            final MediaResourceGateway mediaResourceGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
        this.castMemberGateway = Objects.requireNonNull(castMemberGateway);
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public UpdateVideoOutput execute(final UpdateVideoCommand aCommand) {
        final var anId = VideoID.from(aCommand.id());
        final var aVideo = this.videoGateway.findById(anId)
                .orElseThrow(notFound(anId));

        final var aRating = Rating.of(aCommand.rating()).orElse(null);
        final var aLaunchedAt = aCommand.launchedAt() != null ? Year.of(aCommand.launchedAt()) : null;
        final var aCategories = toIdentifier(aCommand.categories(), CategoryID::from);
        final var aGenres = toIdentifier(aCommand.genres(), GenreID::from);
        final var aCastMembers = toIdentifier(aCommand.castMembers(), CastMemberID::from);

        final var aNotification = Notification.create();
        aNotification.append(validateCategories(aCategories));
        aNotification.append(validateGenres(aGenres));
        aNotification.append(validateCastMembers(aCastMembers));

        final var aBuilder = this.build(aCommand, aRating, aLaunchedAt, aCategories, aGenres, aCastMembers);
        aNotification.validate(() -> aVideo.update(aBuilder));
        if (aNotification.hasErrors()) {
            notify(anId, aNotification);
        }
        return UpdateVideoOutput.from(
                update(aCommand, aVideo));
    }

    private Supplier<NotFoundException> notFound(final Identifier anId) {
        return () -> NotFoundException.with(Video.class, anId);
    }

    private void notify(final Identifier anId, final Notification notification) {
        throw new NotificationException("Could not update Aggregate Video %s"
                .formatted(anId.getValue()), notification);
    }

    private Video update(final UpdateVideoCommand aCommand, final Video aVideo) {
        final var anId = aVideo.getId();
        try {
            if (aCommand.video() != null) {
                final var aVideoMedia = this.mediaResourceGateway.storeAudioVideo(
                        anId, VideoResource.with(aCommand.video(), VideoMediaType.VIDEO));
                aVideo.configureVideo(aVideoMedia);
            }
            if (aCommand.trailer() != null) {
                final var aTrailerMedia = this.mediaResourceGateway.storeAudioVideo(
                        anId, VideoResource.with(aCommand.trailer(), VideoMediaType.TRAILER));
                aVideo.configureTrailer(aTrailerMedia);
            }
            if (aCommand.banner() != null) {
                final var aBannerImage = this.mediaResourceGateway.storeImage(
                        anId, VideoResource.with(aCommand.banner(), VideoMediaType.BANNER));
                aVideo.configureBanner(aBannerImage);
            }
            if (aCommand.thumbnail() != null) {
                final var aThumbnailImage = this.mediaResourceGateway.storeImage(
                        anId, VideoResource.with(aCommand.thumbnail(), VideoMediaType.THUMBNAIL));
                aVideo.configureThumbnail(aThumbnailImage);
            }
            if (aCommand.thumbnailHalf() != null) {
                final var aThumbnailHalfImage = this.mediaResourceGateway.storeImage(
                        anId, VideoResource.with(aCommand.thumbnailHalf(), VideoMediaType.THUMBNAIL_HALF));
                aVideo.configureThumbnailHalf(aThumbnailHalfImage);
            }
            return this.videoGateway.update(aVideo);
        } catch (Exception e) {
            throw InternalErrorException.with("An error on update video was observed [videoId: %s]"
                    .formatted(anId.getValue()), e.getCause());
        }
    }

    private ValidationHandler validateCategories(final Set<CategoryID> ids) {
        return validateAggregate("categories", ids, categoryGateway::existsByIds);
    }

    private ValidationHandler validateGenres(final Set<GenreID> ids) {
        return validateAggregate("genres", ids, genreGateway::existsByIds);
    }

    private ValidationHandler validateCastMembers(final Set<CastMemberID> ids) {
        return validateAggregate("cast members", ids, castMemberGateway::existsByIds);
    }

    private <T> Set<T> toIdentifier(final Set<String> ids, final Function<String, T> mapper) {
        return ids.stream()
                .map(mapper)
                .collect(Collectors.toSet());
    }

    private Video.Builder build(UpdateVideoCommand aCommand, Rating aRating, Year aLaunchedAt, Set<CategoryID> aCategories, Set<GenreID> aGenres, Set<CastMemberID> aCastMembers) {
        return new Video.Builder(aCommand.title(), aCommand.description(),
                aLaunchedAt, aRating)
                .duration(aCommand.duration())
                .releaseStatus(aCommand.releaseStatus())
                .publishingStatus(aCommand.publishingStatus())
                .categories(aCategories)
                .genres(aGenres)
                .castMembers(aCastMembers);
    }
}
