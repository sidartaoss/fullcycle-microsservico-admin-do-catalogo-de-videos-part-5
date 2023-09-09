package com.fullcycle.admin.catalogo.infrastructure.video.models;

import com.fullcycle.admin.catalogo.JacksonTest;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JacksonTest
public class VideoEncoderResultTest {

    @Autowired
    private JacksonTester<VideoEncoderResult> json;

    @Test
    void shouldTestUnmarshalSuccessResult() throws IOException {
        // Given
        final var expectedId = IdUtils.uuid();
        final var expectedOutpuBucket = "codeeducationtest";
        final var expectedStatus = "COMPLETED";
        final var expectedEcodedVideoFolder = "anyfolder";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedFilePath = "any.mp4";
        VideoMetadata expectedMetadata = new VideoMetadata(
                expectedEcodedVideoFolder,
                expectedResourceId,
                expectedFilePath);

        final var json = """
                {
                    "status": "%s",
                    "job_id": "%s",
                    "output_bucket_path": "%s",
                    "video": {
                        "encoded_video_folder": "%s",
                        "resource_id": "%s",
                        "file_path": "%s"
                    }
                }
                """
                .formatted(
                        expectedStatus,
                        expectedId,
                        expectedOutpuBucket,
                        expectedEcodedVideoFolder,
                        expectedResourceId,
                        expectedFilePath
                );

        // When
        final var actualResult = this.json.parse(json);

        // Then
        Assertions.assertThat(actualResult)
                .isInstanceOf(VideoEncoderCompleted.class)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("outputBucket", expectedOutpuBucket)
                .hasFieldOrPropertyWithValue("video", expectedMetadata);

    }

    @Test
    void shouldTestMarshalSuccessResult() throws IOException {
        // Given
        final var expectedId = IdUtils.uuid();
        final var expectedOutpuBucket = "codeeducationtest";
        final var expectedStatus = "COMPLETED";
        final var expectedEcodedVideoFolder = "anyfolder";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedError = "";
        final var expectedCreatedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        VideoMetadata expectedMetadata = new VideoMetadata(
                expectedEcodedVideoFolder,
                expectedResourceId,
                expectedFilePath);

        final var aResult = new VideoEncoderCompleted(
                expectedId, expectedOutpuBucket, expectedMetadata, expectedError, expectedCreatedAt, expectedUpdatedAt);

        // When
        final var actualResult = this.json.write(aResult);

        // Then
        Assertions.assertThat(actualResult)
                .hasJsonPathValue("$.job_id", expectedId)
                .hasJsonPathValue("$.output_bucket_path", expectedOutpuBucket)
                .hasJsonPathValue("$.status", expectedStatus)
                .hasJsonPathValue("$.video.encoded_video_folder", expectedEcodedVideoFolder)
                .hasJsonPathValue("$.video.resource_id", expectedResourceId)
                .hasJsonPathValue("$.video.file_path", expectedFilePath);

    }

    @Test
    void shouldTestUnmarshalErrorResult() throws IOException {
        // Given
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedVideoMessage =
                new VideoMessage(expectedResourceId, expectedFilePath);

        final var json = """
                {
                    "status": "%s",
                    "error": "%s",
                    "message": {
                        "resource_id": "%s",
                        "file_path": "%s"
                    }
                }
                """
                .formatted(
                        expectedStatus,
                        expectedMessage,
                        expectedResourceId,
                        expectedFilePath
                );

        // When
        final var actualResult = this.json.parse(json);

        // Then
        Assertions.assertThat(actualResult)
                .isInstanceOf(VideoEncoderError.class)
                .hasFieldOrPropertyWithValue("error", expectedMessage)
                .hasFieldOrPropertyWithValue("message", expectedVideoMessage);
    }

    @Test
    void shouldTestMarshalErrorResult() throws IOException {
        // Given
        final var expectedMessage = "Resource not found";
        final var expectedStatus = "ERROR";
        final var expectedResourceId = IdUtils.uuid();
        final var expectedFilePath = "any.mp4";
        final var expectedVideoMessage =
                new VideoMessage(expectedResourceId, expectedFilePath);

        final var aResult = new VideoEncoderError(
                expectedVideoMessage, expectedMessage);

        // When
        final var actualResult = this.json.write(aResult);

        // Then
        Assertions.assertThat(actualResult)
                .hasJsonPathValue("$.status", expectedStatus)
                .hasJsonPathValue("$.error", expectedMessage)
                .hasJsonPathValue("$.message.resource_id", expectedResourceId)
                .hasJsonPathValue("$.message.file_path", expectedFilePath);
    }
}
