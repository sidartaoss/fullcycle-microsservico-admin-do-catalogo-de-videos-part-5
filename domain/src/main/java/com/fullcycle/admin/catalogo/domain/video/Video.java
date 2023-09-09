package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.AggregateRoot;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.event.DomainEvent;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalogo.domain.validation.handler.ThrowsValidationHandler;

import java.time.Instant;
import java.time.Year;
import java.util.*;

public class Video extends AggregateRoot<VideoID> {

    private String title;
    private String description;
    private Year launchedAt;
    private double duration;
    private Rating rating;

    private ReleaseStatus releaseStatus;
    private PublishingStatus publishingStatus;

    private final Instant createdAt;
    private Instant updatedAt;

    private ImageMedia banner;
    private ImageMedia thumbnail;
    private ImageMedia thumbnailHalf;

    private AudioVideoMedia trailer;
    private AudioVideoMedia video;

    private Set<CategoryID> categories;
    private Set<GenreID> genres;
    private Set<CastMemberID> castMembers;

    public static class Builder {
        private VideoID id;
        private final String title;
        private final String description;
        private final Year launchedAt;
        private double duration;
        private final Rating rating;
        private ReleaseStatus releaseStatus;
        private PublishingStatus publishingStatus;
        private Instant createdAt;
        private Instant updatedAt;
        private ImageMedia banner;
        private ImageMedia thumbnail;
        private ImageMedia thumbnailHalf;
        private AudioVideoMedia trailer;
        private AudioVideoMedia video;
        private Set<CategoryID> categories;
        private Set<GenreID> genres;
        private Set<CastMemberID> castMembers;
        private List<DomainEvent> domainEvents;

        public Builder(
                final String aTitle,
                final String aDescription,
                final Year aLaunchedAt,
                final Rating aRating) {
            this.title = aTitle;
            this.description = aDescription;
            this.launchedAt = aLaunchedAt;
            this.rating = aRating;
        }

        public Builder id(final VideoID anId) {
            this.id = anId;
            return this;
        }

        public Builder duration(final double aDuration) {
            this.duration = aDuration;
            return this;
        }

        public Builder releaseStatus(final ReleaseStatus aReleaseStatus) {
            this.releaseStatus = aReleaseStatus;
            return this;
        }

        public Builder publishingStatus(final PublishingStatus aPublishingStatus) {
            this.publishingStatus = aPublishingStatus;
            return this;
        }

        public Builder createdAt(final Instant aCreatedAt) {
            this.createdAt = aCreatedAt;
            return this;
        }

        public Builder updatedAt(final Instant anUpdatedAt) {
            this.updatedAt = anUpdatedAt;
            return this;
        }

        public Builder banner(final ImageMedia aBanner) {
            this.banner = aBanner;
            return this;
        }

        public Builder thumbnail(final ImageMedia aThumbnail) {
            this.thumbnail = aThumbnail;
            return this;
        }

        public Builder thumbnailHalf(final ImageMedia aThumbnailHalf) {
            this.thumbnailHalf = aThumbnailHalf;
            return this;
        }

        public Builder trailer(final AudioVideoMedia aTrailer) {
            this.trailer = aTrailer;
            return this;
        }

        public Builder video(final AudioVideoMedia aVideo) {
            this.video = aVideo;
            return this;
        }

        public Builder categories(final Set<CategoryID> aCategories) {
            this.categories = aCategories;
            return this;
        }

        public Builder genres(final Set<GenreID> aGenres) {
            this.genres = aGenres;
            return this;
        }

        public Builder castMembers(final Set<CastMemberID> aCastMembers) {
            this.castMembers = aCastMembers;
            return this;
        }

        public Builder domainEvents(final List<DomainEvent> aDomainEvents) {
            this.domainEvents = aDomainEvents;
            return this;
        }
    }

    private Video(final Builder aBuilder) {
        super(aBuilder.id, aBuilder.domainEvents);
        this.title = aBuilder.title;
        this.description = aBuilder.description;
        this.launchedAt = aBuilder.launchedAt;
        this.duration = aBuilder.duration;
        this.releaseStatus = Objects.requireNonNull(aBuilder.releaseStatus);
        this.publishingStatus = Objects.requireNonNull(aBuilder.publishingStatus);
        this.rating = aBuilder.rating;
        this.createdAt = Objects.requireNonNull(aBuilder.createdAt);
        this.updatedAt = Objects.requireNonNull(aBuilder.updatedAt);
        this.categories = aBuilder.categories;
        this.genres = aBuilder.genres;
        this.castMembers = aBuilder.castMembers;
        this.banner = aBuilder.banner;
        this.thumbnail = aBuilder.thumbnail;
        this.thumbnailHalf = aBuilder.thumbnailHalf;
        this.trailer = aBuilder.trailer;
        this.video = aBuilder.video;
        this.validate(new ThrowsValidationHandler());
    }

    @Override
    public void validate(ValidationHandler aHandler) {
        new VideoValidator(this, aHandler).validate();
    }

    public Video update(final Builder aBuilder) {
        this.title = aBuilder.title;
        this.description = aBuilder.description;
        this.launchedAt = aBuilder.launchedAt;
        this.duration = aBuilder.duration;
        this.releaseStatus = Objects.requireNonNull(aBuilder.releaseStatus);
        this.publishingStatus = Objects.requireNonNull(aBuilder.publishingStatus);
        this.rating = aBuilder.rating;
        this.setCategories(aBuilder.categories);
        this.setGenres(aBuilder.genres);
        this.setCastMembers(aBuilder.castMembers);
        this.configureVideo(aBuilder.video);
        this.configureTrailer(aBuilder.trailer);
        this.configureBanner(aBuilder.banner);
        this.configureThumbnail(aBuilder.thumbnail);
        this.configureThumbnailHalf(aBuilder.thumbnailHalf);
        this.updatedAt = InstantUtils.now();
        this.validate(new ThrowsValidationHandler());
        return this;
    }

    public static Video newVideo(final Builder aBuilder) {
        final var aNow = InstantUtils.now();
        final var anId = VideoID.unique();
        return new Video(new Builder(
                aBuilder.title,
                aBuilder.description,
                aBuilder.launchedAt,
                aBuilder.rating)
                .id(anId)
                .domainEvents(aBuilder.domainEvents)
                .duration(aBuilder.duration)
                .releaseStatus(aBuilder.releaseStatus)
                .publishingStatus(aBuilder.publishingStatus)
                .createdAt(aNow)
                .updatedAt(aNow)
                .categories(aBuilder.categories)
                .genres(aBuilder.genres)
                .castMembers(aBuilder.castMembers)
                .video(aBuilder.video)
                .trailer(aBuilder.trailer)
                .banner(aBuilder.banner)
                .thumbnail(aBuilder.thumbnail)
                .thumbnailHalf(aBuilder.thumbnailHalf)
        );
    }

    public static Video with(final Video aVideo) {
        final var aBuilder = new Builder(aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt(),
                aVideo.getRating())
                .id(aVideo.getId())
                .domainEvents(aVideo.getDomainEvents())
                .duration(aVideo.getDuration())
                .releaseStatus(aVideo.getReleaseStatus())
                .publishingStatus(aVideo.getPublishingStatus())
                .createdAt(aVideo.getCreatedAt())
                .updatedAt(aVideo.getUpdatedAt())
                .banner(aVideo.getBanner())
                .thumbnail(aVideo.getThumbnail())
                .thumbnailHalf(aVideo.getThumbnailHalf())
                .trailer(aVideo.getTrailer())
                .video(aVideo.getVideo())
                .categories(new HashSet<>(aVideo.getCategories()))
                .genres(new HashSet<>(aVideo.getGenres()))
                .castMembers(new HashSet<>(aVideo.getCastMembers()));
        return new Video(aBuilder);
    }

    public static Video with(final Builder aBuilder) {
        return new Video(new Builder(
                aBuilder.title,
                aBuilder.description,
                aBuilder.launchedAt,
                aBuilder.rating)
                .id(aBuilder.id)
                .domainEvents(aBuilder.domainEvents)
                .duration(aBuilder.duration)
                .releaseStatus(aBuilder.releaseStatus)
                .publishingStatus(aBuilder.publishingStatus)
                .createdAt(aBuilder.createdAt)
                .updatedAt(aBuilder.updatedAt)
                .categories(aBuilder.categories)
                .genres(aBuilder.genres)
                .castMembers(aBuilder.castMembers)
                .video(aBuilder.video)
                .trailer(aBuilder.trailer)
                .banner(aBuilder.banner)
                .thumbnail(aBuilder.thumbnail)
                .thumbnailHalf(aBuilder.thumbnailHalf)
        );
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Year getLaunchedAt() {
        return launchedAt;
    }

    public double getDuration() {
        return duration;
    }

    public Rating getRating() {
        return rating;
    }

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }

    public PublishingStatus getPublishingStatus() {
        return publishingStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ImageMedia getBanner() {
        return banner;
    }

    public ImageMedia getThumbnail() {
        return thumbnail;
    }

    public ImageMedia getThumbnailHalf() {
        return thumbnailHalf;
    }

    public AudioVideoMedia getVideo() {
        return video;
    }

    public AudioVideoMedia getTrailer() {
        return trailer;
    }

    public Video configureBanner(final ImageMedia banner) {
        this.banner = banner;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video configureThumbnail(final ImageMedia thumbnail) {
        this.thumbnail = thumbnail;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video configureThumbnailHalf(final ImageMedia thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Video configureTrailer(final AudioVideoMedia trailer) {
        this.trailer = trailer;
        this.updatedAt = InstantUtils.now();
        onAudioVideoUpdated(trailer);
        return this;
    }

    public Video configureVideo(final AudioVideoMedia video) {
        this.video = video;
        this.updatedAt = InstantUtils.now();
        onAudioVideoUpdated(video);
        return this;
    }

    public Video processing(final VideoMediaType aType) {
        if (VideoMediaType.VIDEO == aType) {
            if (getVideo() != null) {
                configureVideo(getVideo().processing());
            }
        } else if (VideoMediaType.TRAILER == aType) {
            if (getTrailer() != null) {
                configureTrailer(getTrailer().processing());
            }
        }
        return this;
    }

    public Video completed(final VideoMediaType aType, final String encodedPath) {
        if (VideoMediaType.VIDEO == aType) {
            if (getVideo() != null) {
                configureVideo(getVideo().completed(encodedPath));
            }
        } else if (VideoMediaType.TRAILER == aType) {
            if (getTrailer() != null) {
                configureTrailer(getTrailer().completed(encodedPath));
            }
        }
        return this;
    }

    public Set<CategoryID> getCategories() {
        return categories != null ? Collections.unmodifiableSet(categories) : Collections.emptySet();
    }

    public Set<GenreID> getGenres() {
        return genres != null ? Collections.unmodifiableSet(genres) : Collections.emptySet();
    }

    public Set<CastMemberID> getCastMembers() {
        return castMembers != null ? Collections.unmodifiableSet(castMembers) : Collections.emptySet();
    }

    private void setCastMembers(final Set<CastMemberID> castMembers) {
        this.castMembers = castMembers != null ? new HashSet<>(castMembers) : Collections.emptySet();
    }

    private void setCategories(final Set<CategoryID> categories) {
        this.categories = categories != null ? new HashSet<>(categories) : Collections.emptySet();
    }

    private void setGenres(final Set<GenreID> genres) {
        this.genres = genres != null ? new HashSet<>(genres) : Collections.emptySet();
    }

    private void onAudioVideoUpdated(AudioVideoMedia audioVideoMedia) {
        if (audioVideoMedia != null && audioVideoMedia.isPendingEncode()) {
            this.registerEvent(new VideoMediaCreated(audioVideoMedia.id(), audioVideoMedia.rawLocation()));
        }
    }
}
