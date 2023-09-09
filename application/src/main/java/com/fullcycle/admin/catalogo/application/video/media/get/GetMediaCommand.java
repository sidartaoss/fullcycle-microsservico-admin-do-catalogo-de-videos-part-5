package com.fullcycle.admin.catalogo.application.video.media.get;

public record GetMediaCommand(
        String videoId,
        String mediaType
) {

    public static GetMediaCommand with(final String aVideoId, final String aMediaType) {
        return new GetMediaCommand(aVideoId, aMediaType);
    }
}
