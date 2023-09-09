package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public record CreateVideoRequest(
        String title,
        String description,
        Double duration,
        @JsonProperty("year_launched") Integer yearLaunched,
        Boolean opened,
        Boolean published,
        String rating,
        @JsonProperty("cast_members") Set<String> castMembers,
        Set<String> categories,
        Set<String> genres
) {
}
