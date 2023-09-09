package com.fullcycle.admin.catalogo.application.video.update;

import com.fullcycle.admin.catalogo.domain.video.PublishingStatus;
import com.fullcycle.admin.catalogo.domain.video.ReleaseStatus;
import com.fullcycle.admin.catalogo.domain.resource.Resource;

import java.util.Set;

public record UpdateVideoCommand(
        String id,
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

    private UpdateVideoCommand(
            String anId,
            String aTitle,
            String aDescription,
            Integer aLaunchedAt,
            Double aDuration,
            ReleaseStatus aReleaseStatus,
            PublishingStatus aPublishingStatus,
            String aRating,
            Set<String> aCategories,
            Set<String> aGenres,
            Set<String> aCastMembers
    ) {
        this(anId,
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
                null,
                null,
                null,
                null,
                null);
    }

    public static UpdateVideoCommand with(
            String anId,
            String aTitle,
            String aDescription,
            Integer aLaunchedAt,
            Double aDuration,
            ReleaseStatus aReleaseStatus,
            PublishingStatus aPublishingStatus,
            String aRating,
            Set<String> aCategories,
            Set<String> aGenres,
            Set<String> aCastMembers,
            Resource aVideo,
            Resource aTrailer,
            Resource aBanner,
            Resource aThumbnail,
            Resource aThumbnailHalf
    ) {
        return new UpdateVideoCommand(
                anId,
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
                aThumbnailHalf);
    }

    public static UpdateVideoCommand with(
            String anId,
            String aTitle,
            String aDescription,
            Integer aLaunchedAt,
            Double aDuration,
            ReleaseStatus aReleaseStatus,
            PublishingStatus aPublishingStatus,
            String aRating,
            Set<String> aCategories,
            Set<String> aGenres,
            Set<String> aCastMembers
    ) {
        return new UpdateVideoCommand(
                anId,
                aTitle,
                aDescription,
                aLaunchedAt,
                aDuration,
                aReleaseStatus,
                aPublishingStatus,
                aRating,
                aCategories,
                aGenres,
                aCastMembers);
    }
}
