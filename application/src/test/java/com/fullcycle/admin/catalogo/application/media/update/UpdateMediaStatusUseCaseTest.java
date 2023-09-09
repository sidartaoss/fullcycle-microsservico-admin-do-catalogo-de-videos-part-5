package com.fullcycle.admin.catalogo.application.media.update;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.application.video.media.update.DefaultUpdateMediaStatusUseCase;
import com.fullcycle.admin.catalogo.application.video.media.update.UpdateMediaStatusCommand;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.video.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.fullcycle.admin.catalogo.domain.utils.IdUtils.videoIdOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UpdateMediaStatusUseCaseTest extends UseCaseTest {

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultUpdateMediaStatusUseCase updateMediaStatusUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, videoGateway);
    }

    @Nested
    @DisplayName("Update a media status with a valid command")
    class UpdateMediaStatusWithValidCommand {

        @Test
        void Given_a_valid_command_for_video_When_calls_update_Then_should_extract_video_id_from_file_path() {
            final var filePath = "filePath=videoId-7fe9895ab23448ab84ffc950f2009a19/type-VIDEO]";
            final var expected = "7fe9895ab23448ab84ffc950f2009a19";
            final var actual = videoIdOf(filePath);
            assertEquals(expected, actual);
        }

        @Test
        void Given_a_valid_command_for_video_When_calls_update_Then_should_update_media_status_and_encoded_location() {
            // Given
            final var expectedStatus = MediaStatus.COMPLETED;
            final var expectedFolder = "encoded_media";
            final var expectedFilename = "videoId-696d02e507824064994be29b4b845214/type-VIDEO";
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

            final var aVideo = Fixture.Videos.systemDesign()
                    .configureVideo(expectedMedia);

            final var anId = videoIdOf(expectedFilename);
            final var expectedId = VideoID.from(anId);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UpdateMediaStatusCommand.with(expectedStatus,
                            expectedId.getValue(),
                            expectedMedia.id(),
                            expectedFolder,
                            expectedFilename);

            // When
            updateMediaStatusUseCase.execute(aCommand);

            // Then
            verify(videoGateway, times(1)).findById(expectedId);

            final var captor = ArgumentCaptor.forClass(Video.class);
            verify(videoGateway, times(1)).update(captor.capture());
            final var actualVideo = captor.getValue();

            assertNull(actualVideo.getTrailer());

            final var actualVideoMedia = actualVideo.getVideo();

            assertEquals(expectedMedia.id(), actualVideoMedia.id());
            assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
            assertEquals(expectedStatus, actualVideoMedia.status());
            assertEquals(expectedFolder.concat("/").concat(expectedFilename),
                    actualVideoMedia.encodedLocation());
        }

        @Test
        void Given_a_valid_command_for_trailer_When_calls_update_Then_should_update_media_status_and_encoded_location() {
            // Given
            final var expectedStatus = MediaStatus.COMPLETED;
            final var expectedFolder = "encoded_media";
            final var expectedFilename = "videoId-696d02e507824064994be29b4b845214/type-VIDEO";
            final var expectedType = VideoMediaType.TRAILER;
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

            final var aVideo = Fixture.Videos.systemDesign()
                    .configureTrailer(expectedMedia);

            final var anId = videoIdOf(expectedFilename);
            final var expectedId = VideoID.from(anId);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UpdateMediaStatusCommand.with(expectedStatus,
                            expectedId.getValue(),
                            expectedMedia.id(),
                            expectedFolder,
                            expectedFilename);

            // When
            updateMediaStatusUseCase.execute(aCommand);

            // Then
            verify(videoGateway, times(1)).findById(expectedId);

            final var captor = ArgumentCaptor.forClass(Video.class);
            verify(videoGateway, times(1)).update(captor.capture());
            final var actualVideo = captor.getValue();

            assertNull(actualVideo.getVideo());

            final var actualVideoMedia = actualVideo.getTrailer();

            assertEquals(expectedMedia.id(), actualVideoMedia.id());
            assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
            assertEquals(expectedStatus, actualVideoMedia.status());
            assertEquals(expectedFolder.concat("/").concat(expectedFilename),
                    actualVideoMedia.encodedLocation());
        }

        @Test
        void Given_a_valid_command_for_video_When_calls_update_for_processing_Then_should_update_media_status_and_encoded_location() {
            // Given
            final var expectedStatus = MediaStatus.PROCESSING;
            final String expectedFolder = null;
            final String expectedFilename = "videoId-696d02e507824064994be29b4b845214/type-VIDEO";
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

            final var aVideo = Fixture.Videos.systemDesign()
                    .configureVideo(expectedMedia);

            final var anId = videoIdOf(expectedFilename);
            final var expectedId = VideoID.from(anId);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UpdateMediaStatusCommand.with(expectedStatus,
                            expectedId.getValue(),
                            expectedMedia.id(),
                            expectedFolder,
                            expectedFilename);

            // When
            updateMediaStatusUseCase.execute(aCommand);

            // Then
            verify(videoGateway, times(1)).findById(expectedId);

            final var captor = ArgumentCaptor.forClass(Video.class);
            verify(videoGateway, times(1)).update(captor.capture());
            final var actualVideo = captor.getValue();

            assertNull(actualVideo.getTrailer());

            final var actualVideoMedia = actualVideo.getVideo();

            assertEquals(expectedMedia.id(), actualVideoMedia.id());
            assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
            assertEquals(expectedStatus, actualVideoMedia.status());
            assertTrue(actualVideoMedia.encodedLocation().isBlank());
        }

        @Test
        void Given_a_valid_command_for_trailer_When_calls_update_for_processing_Then_should_update_media_status_and_encoded_location() {
            // Given
            final var expectedStatus = MediaStatus.PROCESSING;
            final String expectedFolder = null;
            final String expectedFilename = "videoId-696d02e507824064994be29b4b845214/type-VIDEO";
            final var expectedType = VideoMediaType.TRAILER;
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

            final var aVideo = Fixture.Videos.systemDesign()
                    .configureTrailer(expectedMedia);

            final var anId = videoIdOf(expectedFilename);
            final var expectedId = VideoID.from(anId);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UpdateMediaStatusCommand.with(expectedStatus,
                            expectedId.getValue(),
                            expectedMedia.id(),
                            expectedFolder,
                            expectedFilename);

            // When
            updateMediaStatusUseCase.execute(aCommand);

            // Then
            verify(videoGateway, times(1)).findById(expectedId);

            final var captor = ArgumentCaptor.forClass(Video.class);
            verify(videoGateway, times(1)).update(captor.capture());
            final var actualVideo = captor.getValue();

            assertNull(actualVideo.getVideo());

            final var actualVideoMedia = actualVideo.getTrailer();

            assertEquals(expectedMedia.id(), actualVideoMedia.id());
            assertEquals(expectedMedia.rawLocation(), actualVideoMedia.rawLocation());
            assertEquals(expectedStatus, actualVideoMedia.status());
            assertTrue(actualVideoMedia.encodedLocation().isBlank());
        }
    }

    @Nested
    @DisplayName("Upload a media status with an invalid command")
    class UpdateMediaStatusWithInvalidCommand {

        @Test
        void Given_an_invalid_media_id_for_video_When_calls_update_media_status_Then_should_do_nothing() {
            // Given
            final var expectedStatus = MediaStatus.COMPLETED;
            final var expectedFolder = "encoded_media";
            final var expectedFilename = "videoId-696d02e507824064994be29b4b845214/type-VIDEO";
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);
            final var anInvalidRandomMediaId = "an-invalid-random-media-id";

            final var aVideo = Fixture.Videos.systemDesign()
                    .configureVideo(expectedMedia);

            final var anId = videoIdOf(expectedFilename);
            final var expectedId = VideoID.from(anId);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));

            final var aCommand =
                    UpdateMediaStatusCommand.with(expectedStatus,
                            expectedId.getValue(),
                            anInvalidRandomMediaId,
                            expectedFolder,
                            expectedFilename);

            // When
            updateMediaStatusUseCase.execute(aCommand);

            // Then
            verify(videoGateway, times(1)).findById(expectedId);
            verify(videoGateway, never()).update(any());
        }
    }
}
