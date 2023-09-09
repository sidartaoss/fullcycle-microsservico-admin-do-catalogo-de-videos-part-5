package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.domain.resource.Resource;

public record GetMediaOutput(
        byte[] content,
        String contentType,
        String name
) {

    public static GetMediaOutput from(final Resource aResource) {
        return new GetMediaOutput(
                aResource.content(),
                aResource.contentType(),
                aResource.name()
        );
    }
}
