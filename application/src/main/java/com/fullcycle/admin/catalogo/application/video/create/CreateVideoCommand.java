package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.PublishingStatus;
import com.fullcycle.admin.catalogo.domain.video.ReleaseStatus;

import java.util.Set;

public record CreateVideoCommand(
        String title,
        String description,
        Integer launchedAt,
        Double duration,
        ReleaseStatus releaseStatus,
        PublishingStatus publishingStatus,
        String rating,
        Set<String> categories,
        Set<String> genres,
        Set<String> castMembers,
        Resource video,
        Resource trailer,
        Resource banner,
        Resource thumbnail,
        Resource thumbnailHalf
) {

    private CreateVideoCommand(
            final String aTitle,
            final String aDescription,
            final Integer aLaunchedAt,
            final Double aDuration,
            final ReleaseStatus aReleaseStatus,
            final PublishingStatus aPublishingStatus,
            final String aRating,
            final Set<String> aCategories,
            final Set<String> aGenres,
            final Set<String> aCastMembers
    ) {
        this(aTitle,
                aDescription,
                aLaunchedAt,
                aDuration,
                aReleaseStatus,
                aPublishingStatus,
                aRating,
                aCategories,
                aGenres,
                aCastMembers,
                null,
                null,
                null,
                null,
                null);
    }

    public static CreateVideoCommand with(
            final String aTitle,
            final String aDescription,
            final Integer aLaunchedAt,
            final Double aDuration,
            final ReleaseStatus aReleaseStatus,
            final PublishingStatus aPublishingStatus,
            final String aRating,
            final Set<String> aCategories,
            final Set<String> aGenres,
            final Set<String> aCastMembers,
            final Resource aVideo,
            final Resource aTrailer,
            final Resource aBanner,
            final Resource aThumbnail,
            final Resource aThumbnailHalf
    ) {
        return new CreateVideoCommand(
                aTitle,
                aDescription,
                aLaunchedAt,
                aDuration,
                aReleaseStatus,
                aPublishingStatus,
                aRating,
                aCategories,
                aGenres,
                aCastMembers,
                aVideo,
                aTrailer,
                aBanner,
                aThumbnail,
                aThumbnailHalf
        );
    }

    public static CreateVideoCommand with(
            final String aTitle,
            final String aDescription,
            final Integer aLaunchedAt,
            final Double aDuration,
            final ReleaseStatus aReleaseStatus,
            final PublishingStatus aPublishingStatus,
            final String aRating,
            final Set<String> aCategories,
            final Set<String> aGenres,
            final Set<String> aCastMembers
    ) {
        return new CreateVideoCommand(
                aTitle,
                aDescription,
                aLaunchedAt,
                aDuration,
                aReleaseStatus,
                aPublishingStatus,
                aRating,
                aCategories,
                aGenres,
                aCastMembers
        );
    }
}
