package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.Set;

public record GetVideoByIdResponse(
        String id,
        String title,
        String description,
        @JsonProperty("year_launched") int yearLaunched,
        double duration,
        boolean opened,
        boolean published,
        String rating,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt,
        ImageMediaResponse banner,
        ImageMediaResponse thumbnail,
        @JsonProperty("thumbnail_half") ImageMediaResponse thumbnailHalf,
        AudioVideoMediaResponse video,
        AudioVideoMediaResponse trailer,
        @JsonProperty("categories_id") Set<String> categories,
        @JsonProperty("genres_id") Set<String> genres,
        @JsonProperty("cast_members_id") Set<String> castMembers
) {
}
