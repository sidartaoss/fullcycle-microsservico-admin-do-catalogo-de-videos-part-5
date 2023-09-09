package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.Instant;

@JsonTypeName("COMPLETED")
public record VideoEncoderCompleted (
        @JsonProperty("job_id") String id,
        @JsonProperty("output_bucket_path") String outputBucket,
        @JsonProperty("video") VideoMetadata video,
        @JsonProperty("Error") String error,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt

        ) implements VideoEncoderResult {

    private static final String COMPLETED = "COMPLETED";

    @Override
    public String getStatus() {
        return COMPLETED;
    }
}
