package com.fullcycle.admin.catalogo.application.video.retrieve.get;

import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.Video;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;

import java.util.Objects;
import java.util.function.Supplier;

public class DefaultGetVideoByIdUseCase extends GetVideoByIdUseCase {

    private final VideoGateway videoGateway;

    public DefaultGetVideoByIdUseCase(final VideoGateway videoGateway) {
        this.videoGateway = Objects.requireNonNull(videoGateway);
    }

    @Override
    public GetVideoByIdOutput execute(String anId) {
        final var aVideoID = VideoID.from(anId);
        return this.videoGateway.findById(aVideoID)
                .map(GetVideoByIdOutput::from)
                .orElseThrow(notFound(aVideoID));
    }

    private Supplier<NotFoundException> notFound(final VideoID anId) {
        return () -> NotFoundException.with(Video.class, anId);
    }
}
