package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.CollectionUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.converter.PublishingStatusToBooleanConverter;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.converter.ReleaseStatusToBooleanConverter;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "Video")
@Table(name = "videos")
public class VideoJpaEntity {

    @Id
    private String id;

    private String title;

    private String description;

    @Column(name = "year_launched")
    private int yearLaunched;

    @Convert(converter = ReleaseStatusToBooleanConverter.class)
    @Column(name = "opened")
    private ReleaseStatus releaseStatus;

    @Convert(converter = PublishingStatusToBooleanConverter.class)
    @Column(name = "published")
    private PublishingStatus publishingStatus;

    private Rating rating;

    private double duration;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "video_id")
    private AudioVideoMediaJpaEntity video;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "trailer_id")
    private AudioVideoMediaJpaEntity trailer;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "banner_id")
    private ImageMediaJpaEntity banner;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_id")
    private ImageMediaJpaEntity thumbnail;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "thumbnail_half_id")
    private ImageMediaJpaEntity thumbnailHalf;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCategoryJpaEntity> categories = new HashSet<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoGenreJpaEntity> genres = new HashSet<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<VideoCastMemberJpaEntity> castMembers = new HashSet<>();

    public VideoJpaEntity() {
    }

    public static class Builder {
        private String id;
        private final String title;
        private final String description;
        private final int yearLaunched;
        private ReleaseStatus releaseStatus;
        private PublishingStatus publishingStatus;
        private final Rating rating;
        private double duration;
        private Instant createdAt;
        private Instant updatedAt;
        private AudioVideoMediaJpaEntity video;
        private AudioVideoMediaJpaEntity trailer;
        private ImageMediaJpaEntity banner;
        private ImageMediaJpaEntity thumbnail;
        private ImageMediaJpaEntity thumbnailHalf;

        public Builder(
                final String aTitle,
                final String aDescription,
                final int aYearLaunched,
                final Rating aRating
        ) {
            this.title = aTitle;
            this.description = aDescription;
            this.yearLaunched = aYearLaunched;
            this.rating = aRating;
        }

        public Builder id(final String anId) {
            this.id = anId;
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

        public Builder duration(final double aDuration) {
            this.duration = aDuration;
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

        public Builder video(final AudioVideoMediaJpaEntity aVideo) {
            this.video = aVideo;
            return this;
        }

        public Builder trailer(final AudioVideoMediaJpaEntity aTrailer) {
            this.trailer = aTrailer;
            return this;
        }

        public Builder banner(final ImageMediaJpaEntity aBanner) {
            this.banner = aBanner;
            return this;
        }

        public Builder thumbnail(final ImageMediaJpaEntity aThumbnail) {
            this.thumbnail = aThumbnail;
            return this;
        }

        public Builder thumbnailHalf(final ImageMediaJpaEntity aThumbnailHalf) {
            this.thumbnailHalf = aThumbnailHalf;
            return this;
        }
    }

    private VideoJpaEntity(final Builder aBuilder) {
        this.id = aBuilder.id;
        this.title = aBuilder.title;
        this.description = aBuilder.description;
        this.yearLaunched = aBuilder.yearLaunched;
        this.releaseStatus = aBuilder.releaseStatus;
        this.publishingStatus = aBuilder.publishingStatus;
        this.rating = aBuilder.rating;
        this.duration = aBuilder.duration;
        this.createdAt = aBuilder.createdAt;
        this.updatedAt = aBuilder.updatedAt;
        this.video = aBuilder.video;
        this.trailer = aBuilder.trailer;
        this.banner = aBuilder.banner;
        this.thumbnail = aBuilder.thumbnail;
        this.thumbnailHalf = aBuilder.thumbnailHalf;
    }

    public static VideoJpaEntity from(final Video aVideo) {
        final var entity = new VideoJpaEntity(new Builder(
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt().getValue(),
                aVideo.getRating())
                .id(aVideo.getId().getValue())
                .releaseStatus(aVideo.getReleaseStatus())
                .publishingStatus(aVideo.getPublishingStatus())
                .duration(aVideo.getDuration())
                .createdAt(aVideo.getCreatedAt())
                .updatedAt(aVideo.getUpdatedAt())
                .video(audioVideoMediaJpa(aVideo.getVideo()))
                .trailer(audioVideoMediaJpa(aVideo.getTrailer()))
                .banner(imageMediaJpa(aVideo.getBanner()))
                .thumbnail(imageMediaJpa(aVideo.getThumbnail()))
                .thumbnailHalf(imageMediaJpa(aVideo.getThumbnailHalf()))
        );
        aVideo.getCategories()
                .forEach(entity::addCategory);
        aVideo.getGenres()
                .forEach(entity::addGenre);
        aVideo.getCastMembers()
                .forEach(entity::addCastMember);
        return entity;
    }

    public Video toAggregate() {
        return Video.with(new Video.Builder(
                getTitle(),
                getDescription(),
                Year.of(getYearLaunched()),
                getRating())
                .id(VideoID.from(getId()))
                .releaseStatus(getReleaseStatus())
                .publishingStatus(getPublishingStatus())
                .duration(getDuration())
                .createdAt(getCreatedAt())
                .updatedAt(getUpdatedAt())
                .video(videoDomain())
                .trailer(trailerDomain())
                .banner(bannerDomain())
                .thumbnail(thumbnailDomain())
                .thumbnailHalf(thumbnailHalfDomain())
                .categories(categoriesDomain())
                .genres(genresDomain())
                .castMembers(castMembersDomain()));
    }

    private void addCategory(final CategoryID anId) {
        this.categories.add(VideoCategoryJpaEntity.from(this, anId));
    }

    private void addGenre(final GenreID anId) {
        this.genres.add(VideoGenreJpaEntity.from(this, anId));
    }

    private void addCastMember(final CastMemberID anId) {
        this.castMembers.add(VideoCastMemberJpaEntity.from(this, anId));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYearLaunched() {
        return yearLaunched;
    }

    public void setYearLaunched(int yearLaunched) {
        this.yearLaunched = yearLaunched;
    }

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(ReleaseStatus releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public PublishingStatus getPublishingStatus() {
        return publishingStatus;
    }

    public void setPublishingStatus(PublishingStatus publishingStatus) {
        this.publishingStatus = publishingStatus;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AudioVideoMediaJpaEntity getVideo() {
        return video;
    }

    public void setVideo(AudioVideoMediaJpaEntity video) {
        this.video = video;
    }

    public AudioVideoMediaJpaEntity getTrailer() {
        return trailer;
    }

    public void setTrailer(AudioVideoMediaJpaEntity trailer) {
        this.trailer = trailer;
    }

    public ImageMediaJpaEntity getBanner() {
        return banner;
    }

    public void setBanner(ImageMediaJpaEntity banner) {
        this.banner = banner;
    }

    public ImageMediaJpaEntity getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ImageMediaJpaEntity thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageMediaJpaEntity getThumbnailHalf() {
        return thumbnailHalf;
    }

    public void setThumbnailHalf(ImageMediaJpaEntity thumbnailHalf) {
        this.thumbnailHalf = thumbnailHalf;
    }

    public Set<VideoCategoryJpaEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<VideoCategoryJpaEntity> categories) {
        this.categories = categories;
    }

    public Set<VideoGenreJpaEntity> getGenres() {
        return genres;
    }

    public void setGenres(Set<VideoGenreJpaEntity> genres) {
        this.genres = genres;
    }

    public Set<VideoCastMemberJpaEntity> getCastMembers() {
        return castMembers;
    }

    public void setCastMembers(Set<VideoCastMemberJpaEntity> castMembers) {
        this.castMembers = castMembers;
    }

    public Set<CategoryID> getCategoriesID() {
        return CollectionUtils.mapTo(getCategories(), it -> CategoryID.from(it.getId().getCategoryId()));
    }

    public Set<GenreID> getGenresID() {
        return CollectionUtils.mapTo(getGenres(), it -> GenreID.from(it.getId().getGenreId()));
    }

    public Set<CastMemberID> getCastMembersID() {
        return CollectionUtils.mapTo(getCastMembers(), it -> CastMemberID.from(it.getId().getCastMemberId()));
    }

    private AudioVideoMedia videoDomain() {
        return getVideo() != null ? getVideo().toDomain() : null;
    }

    private AudioVideoMedia trailerDomain() {
        return getTrailer() != null ? getTrailer().toDomain() : null;
    }

    private ImageMedia bannerDomain() {
        return getBanner() != null ? getBanner().toDomain() : null;
    }

    private ImageMedia thumbnailDomain() {
        return getThumbnail() != null ? getThumbnail().toDomain() : null;
    }

    private ImageMedia thumbnailHalfDomain() {
        return getThumbnailHalf() != null ? getThumbnailHalf().toDomain() : null;
    }

    private Set<CategoryID> categoriesDomain() {
        return getCategories().stream()
                .map(it -> CategoryID.from(it.getId().getCategoryId()))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<GenreID> genresDomain() {
        return getGenres().stream()
                .map(it -> GenreID.from(it.getId().getGenreId()))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Set<CastMemberID> castMembersDomain() {
        return getCastMembers().stream()
                .map(it -> CastMemberID.from(it.getId().getCastMemberId()))
                .collect(Collectors.toUnmodifiableSet());
    }

    private static AudioVideoMediaJpaEntity audioVideoMediaJpa(final AudioVideoMedia anAudioVideoMedia) {
        return anAudioVideoMedia != null ? AudioVideoMediaJpaEntity.from(anAudioVideoMedia) : null;
    }

    private static ImageMediaJpaEntity imageMediaJpa(final ImageMedia anImageMedia) {
        return anImageMedia != null ? ImageMediaJpaEntity.from(anImageMedia) : null;
    }
}
