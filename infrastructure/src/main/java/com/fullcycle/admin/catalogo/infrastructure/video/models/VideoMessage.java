package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public record VideoMessage(
        @JsonProperty("resource_id") String resourceId,
        @JsonProperty("file_path") String filePath
) implements Serializable {
}
