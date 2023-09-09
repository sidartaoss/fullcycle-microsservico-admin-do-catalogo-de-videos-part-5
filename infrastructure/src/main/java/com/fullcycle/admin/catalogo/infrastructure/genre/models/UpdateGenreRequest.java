package com.fullcycle.admin.catalogo.infrastructure.genre.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

public record UpdateGenreRequest(
        String name,
        @JsonProperty("categories_id") List<String> categories
) {

    public List<String> categories() {
        return this.categories != null ? categories : Collections.emptyList();
    }
}
