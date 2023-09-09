package com.fullcycle.admin.catalogo.application.video.media.update;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.*;

import java.util.Objects;

import static com.fullcycle.admin.catalogo.domain.utils.IdUtils.videoIdOf;

public class DefaultUpdateMediaStatusUseCase extends UpdateMediaStatusUseCase {

    private final VideoGateway videoGateway;

    public DefaultUpdateMediaStatusUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public void execute(UpdateMediaStatusCommand aCommand) {
        final var anId = videoIdOf(aCommand.filename());
        final var aVideoId = VideoID.from(anId);
        final var aResourceId = aCommand.resourceId();
        final var aFolder = aCommand.folder();
        final var aFilename = aCommand.filename();
        final var aVideo = this.videoGateway.findById(aVideoId)
                .orElseThrow(() -> notFound(aVideoId));

        final var encodedPath = "%s/%s".formatted(aFolder, aFilename);

        if (matches(aResourceId, aVideo.getVideo())) {
            updateVideo(VideoMediaType.VIDEO, aCommand.status(), aVideo, encodedPath);
            this.videoGateway.update(aVideo);
        } else if (matches(aResourceId, aVideo.getTrailer())) {
            updateVideo(VideoMediaType.TRAILER, aCommand.status(), aVideo, encodedPath);
            this.videoGateway.update(aVideo);
        }
    }

    private void updateVideo(
             final VideoMediaType aType,
             final MediaStatus aStatus,
             final Video aVideo,
             final String encodedPath) {
        switch (aStatus) {
            case PENDING -> {}
            case PROCESSING -> aVideo.processing(aType);
            case COMPLETED -> aVideo.completed(aType, encodedPath);
        }
    }

    private boolean matches(final String aResourceId, final AudioVideoMedia anAudioVideoMedia) {
        return anAudioVideoMedia != null && anAudioVideoMedia.id().equals(aResourceId);
    }

    private NotFoundException notFound(final VideoID anId) {
        return NotFoundException.with(Video.class, anId);
    }
}
