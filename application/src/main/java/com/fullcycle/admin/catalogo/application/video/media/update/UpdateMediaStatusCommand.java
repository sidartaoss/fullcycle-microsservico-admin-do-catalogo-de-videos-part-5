package com.fullcycle.admin.catalogo.application.video.media.update;

import com.fullcycle.admin.catalogo.domain.video.MediaStatus;

public record UpdateMediaStatusCommand(
        MediaStatus status,
        String videoId,
        String resourceId,
        String folder,
        String filename
) {

    public static UpdateMediaStatusCommand with(
            final MediaStatus aStatus,
            final String aVideoId,
            final String aResourceId,
            final String aFolder,
            final String aFilename
    ) {
        return new UpdateMediaStatusCommand(aStatus, aVideoId, aResourceId, aFolder, aFilename);
    }

    public static UpdateMediaStatusCommand with(
            final MediaStatus aStatus,
            final String aResourceId,
            final String aFilename
    ) {
        return new UpdateMediaStatusCommand(aStatus, "", aResourceId, "", aFilename);
    }
}
