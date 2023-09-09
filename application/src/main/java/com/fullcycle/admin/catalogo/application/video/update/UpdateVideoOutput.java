package com.fullcycle.admin.catalogo.application.video.update;

import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

public record UpdateVideoOutput(
        String id
) {

    public static UpdateVideoOutput from(final VideoID anId) {
        return new UpdateVideoOutput(anId.getValue());
    }

    public static UpdateVideoOutput from(final Video aVideo) {
        return from(aVideo.getId());
    }
}
