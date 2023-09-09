package com.fullcycle.admin.catalogo.infrastructure.video.presenters;

import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.VideoListOutput;
import com.fullcycle.admin.catalogo.domain.video.AudioVideoMedia;
import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import com.fullcycle.admin.catalogo.domain.video.PublishingStatus;
import com.fullcycle.admin.catalogo.domain.video.ReleaseStatus;
import com.fullcycle.admin.catalogo.infrastructure.video.models.AudioVideoMediaResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.GetVideoByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.ImageMediaResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoListResponse;

public interface VideoApiPresenter {

    static GetVideoByIdResponse present(final GetVideoByIdOutput output) {
        return new GetVideoByIdResponse(
                output.id(),
                output.title(),
                output.description(),
                output.launchedAt(),
                output.duration(),
                openedOf(output.releaseStatus()),
                publishedOf(output.publishingStatus()),
                output.rating(),
                output.createdAt(),
                output.updatedAt(),
                present(output.banner()),
                present(output.thumbnail()),
                present(output.thumbnailHalf()),
                present(output.video()),
                present(output.trailer()),
                output.categories(),
                output.genres(),
                output.castMembers()
        );
    }

    static ImageMediaResponse present(final ImageMedia anImageMedia) {
        if (anImageMedia == null) {
            return null;
        }
        return new ImageMediaResponse(
                anImageMedia.id(),
                anImageMedia.checksum(),
                anImageMedia.name(),
                anImageMedia.location()
        );
    }

    static AudioVideoMediaResponse present(final AudioVideoMedia anAudioVideoMedia) {
        if (anAudioVideoMedia == null) {
            return null;
        }
        return new AudioVideoMediaResponse(
                anAudioVideoMedia.id(),
                anAudioVideoMedia.checksum(),
                anAudioVideoMedia.name(),
                anAudioVideoMedia.rawLocation(),
                anAudioVideoMedia.encodedLocation(),
                anAudioVideoMedia.status().name()
        );
    }

    static VideoListResponse present(final VideoListOutput output) {
        return new VideoListResponse(
                output.id(),
                output.title(),
                output.description(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    private static boolean openedOf(final ReleaseStatus aReleaseStatus) {
        return ReleaseStatus.RELEASED == aReleaseStatus;
    }

    private static boolean publishedOf(final PublishingStatus aPublishingStatus) {
        return PublishingStatus.PUBLISHED == aPublishingStatus;
    }
}
