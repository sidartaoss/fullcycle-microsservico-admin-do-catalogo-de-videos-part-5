package com.fullcycle.admin.catalogo.infrastructure.amqp;

import com.fullcycle.admin.catalogo.AmqpTest;
import com.fullcycle.admin.catalogo.application.video.media.update.UpdateMediaStatusCommand;
import com.fullcycle.admin.catalogo.application.video.media.update.UpdateMediaStatusUseCase;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.MediaStatus;
import com.fullcycle.admin.catalogo.infrastructure.configuration.annotations.VideoEncodedQueue;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalogo.infrastructure.configuration.properties.amqp.QueueProperties;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoEncoderCompleted;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoEncoderError;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoMessage;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoMetadata;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.test.RabbitListenerTestHarness;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@AmqpTest
public class VideoEncoderListenerTest {

    @Autowired
    private TestRabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitListenerTestHarness harness;

    @MockBean
    private UpdateMediaStatusUseCase updateMediaStatusUseCase;

    @Autowired
    @VideoEncodedQueue
    private QueueProperties queueProperties;

    @Nested
    @DisplayName("Listen on error result")
    class OnErrorResult {

        /**
        @Test
        void Given_an_error_result_When_calls_listener_Then_should_process_it() throws InterruptedException {
            // Given
            final var expectedError = new VideoEncoderError(
                    new VideoMessage("123", "abc"), "Video not found");

            final var expectedMessage = Json.writeValueAsString(expectedError);

            // When
            rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

            // Then
            final var invocationData =
                    harness.getNextInvocationDataFor(
                            VideoEncoderListener.LISTENER_ID, 1, TimeUnit.SECONDS);
            assertNotNull(invocationData);
            assertNotNull(invocationData.getArguments());
            final var actualMessage = (String) invocationData.getArguments()[0];
            assertEquals(expectedMessage, actualMessage);
        }
        **/
    }

    @Nested
    @DisplayName("Listen on error result")
    class OnSuccessResult {

        /**
        @Test
        void Given_a_completed_result_When_calls_listener_Then_should_call_use_case() throws InterruptedException {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedOutputBucket = "codeeducationtest";
            final var expectedEcoderVideoFolder = "anyfolder";
            final var expectedStatus = MediaStatus.COMPLETED;
            final var expectedResourceId = IdUtils.uuid();
            final var expectedFilePath = "any.mp4";
            final var expectedError = "";
            final var expectedCreatedAt = Instant.now();
            final var expectedUpdatedAt = Instant.now();
            VideoMetadata expectedMetadata = new VideoMetadata(
                    expectedEcoderVideoFolder,
                    expectedResourceId,
                    expectedFilePath);

            final var aResult = new VideoEncoderCompleted(
                    expectedId,
                    expectedOutputBucket,
                    expectedMetadata,
                    expectedError,
                    expectedCreatedAt,
                    expectedUpdatedAt);

            final var expectedMessage = Json.writeValueAsString(aResult);

            doNothing()
                    .when(updateMediaStatusUseCase).execute(any());

            // When
            rabbitTemplate.convertAndSend(queueProperties.getQueue(), expectedMessage);

            // Then
            final var invocationData =
                    harness.getNextInvocationDataFor(
                            VideoEncoderListener.LISTENER_ID, 1, TimeUnit.SECONDS);
            assertNotNull(invocationData);
            assertNotNull(invocationData.getArguments());
            final var actualMessage = (String) invocationData.getArguments()[0];
            assertEquals(expectedMessage, actualMessage);

            final var cmdCaptor = ArgumentCaptor.forClass(UpdateMediaStatusCommand.class);
            verify(updateMediaStatusUseCase, times(1)).execute(cmdCaptor.capture());
            final var actualCommand = cmdCaptor.getValue();
            assertEquals(expectedStatus, actualCommand.status());
            assertEquals(expectedId, actualCommand.videoId());
            assertEquals(expectedResourceId, actualCommand.resourceId());
            assertEquals(expectedEcoderVideoFolder, actualCommand.folder());
            assertEquals(expectedFilePath, actualCommand.filename());
        }
        **/
    }
}
