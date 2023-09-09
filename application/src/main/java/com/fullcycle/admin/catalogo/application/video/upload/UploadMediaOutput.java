package com.fullcycle.admin.catalogo.application.video.upload;

import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;

public record UploadMediaOutput(
        String videoId,
        VideoMediaType mediaType
) {

    public static UploadMediaOutput from(final String anId, final VideoMediaType aMediaType) {
        return new UploadMediaOutput(
                anId,
                aMediaType
        );
    }

    public static UploadMediaOutput from(final Video aVideo, final VideoMediaType aMediaType) {
        return new UploadMediaOutput(
                aVideo.getId().getValue(),
                aMediaType
        );
    }
}
