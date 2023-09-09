package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

public record CreateVideoOutput(
        String id
) {

    public static CreateVideoOutput from(final VideoID anId) {
        return new CreateVideoOutput(anId.getValue());
    }

    public static CreateVideoOutput from(final Video aVideo) {
        return from(aVideo.getId());
    }
}
