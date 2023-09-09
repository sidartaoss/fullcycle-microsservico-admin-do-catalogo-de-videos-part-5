package com.fullcycle.admin.catalogo.application.video.media.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetMediaUseCase extends GetMediaUseCase {

    private final MediaResourceGateway mediaResourceGateway;

    public DefaultGetMediaUseCase(final MediaResourceGateway mediaResourceGateway) {
        this.mediaResourceGateway = Objects.requireNonNull(mediaResourceGateway);
    }

    @Override
    public GetMediaOutput execute(final GetMediaCommand aCommand) {
        final var anId = VideoID.from(aCommand.videoId());
        final var aType = VideoMediaType.of(aCommand.mediaType())
                .orElseThrow(typeNotFound(aCommand.mediaType()));
        final var aResource =
                this.mediaResourceGateway.getResource(anId, aType)
                        .orElseThrow(notFound(anId.getValue(), aType.name()));
        return GetMediaOutput.from(aResource);
    }

    private Supplier<NotFoundException> typeNotFound(final String aType) {
        return () -> NotFoundException.with(new Error("Media type %s does not exist."
                .formatted(aType)));
    }

    private Supplier<NotFoundException> notFound(final String anId, final String aType) {
        return () -> NotFoundException.with(new Error("Resource %s not found for video %s"
                .formatted(aType, anId)));
    }
}
