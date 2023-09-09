package com.fullcycle.admin.catalogo.infrastructure.video.models;

public record ImageMediaResponse(
        String id,
        String checksum,
        String name,
        String location
) {
}
