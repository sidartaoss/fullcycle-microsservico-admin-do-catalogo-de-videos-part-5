package com.fullcycle.admin.catalogo.application.video.retrieve.get;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.video.*;

import java.time.Instant;
import java.util.Set;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;

public record GetVideoByIdOutput(
        String id,
        String title,
        String description,
        int launchedAt,
        double duration,
        ReleaseStatus releaseStatus,
        PublishingStatus publishingStatus,
        String rating,
        Set<String> categories,
        Set<String> genres,
        Set<String> castMembers,
        ImageMedia banner,
        ImageMedia thumbnail,
        ImageMedia thumbnailHalf,
        AudioVideoMedia trailer,
        AudioVideoMedia video,
        Instant createdAt,
        Instant updatedAt
) {

    public static GetVideoByIdOutput from(final Video aVideo) {
        return new GetVideoByIdOutput(
                aVideo.getId().getValue(),
                aVideo.getTitle(),
                aVideo.getDescription(),
                aVideo.getLaunchedAt().getValue(),
                aVideo.getDuration(),
                aVideo.getReleaseStatus(),
                aVideo.getPublishingStatus(),
                aVideo.getRating().getName(),
                mapTo(aVideo.getCategories(), Identifier::getValue),
                mapTo(aVideo.getGenres(), Identifier::getValue),
                mapTo(aVideo.getCastMembers(), Identifier::getValue),
                aVideo.getBanner(),
                aVideo.getThumbnail(),
                aVideo.getThumbnailHalf(),
                aVideo.getTrailer(),
                aVideo.getVideo(),
                aVideo.getCreatedAt(),
                aVideo.getUpdatedAt()
        );
    }
}

