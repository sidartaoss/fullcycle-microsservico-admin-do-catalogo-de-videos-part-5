package com.fullcycle.admin.catalogo.application.video.upload;

import com.fullcycle.admin.catalogo.domain.video.VideoResource;

public record UploadMediaCommand(
        String videoId,
        VideoResource videoResource
) {

    public static UploadMediaCommand with(final String aVideoId, final VideoResource aVideoResource) {
        return new UploadMediaCommand(aVideoId, aVideoResource);
    }
}
