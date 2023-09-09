package com.fullcycle.admin.catalogo.application.media.upload;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.application.video.upload.DefaultUploadMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.upload.UploadMediaCommand;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.fullcycle.admin.catalogo.domain.video.VideoResource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UploadMediaUseCaseTest extends UseCaseTest {

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultUploadMediaUseCase uploadMediaUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway, videoGateway);
    }

    @Nested
    @DisplayName("Upload a resource with a valid command")
    class UploadResourceWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_upload_Then_should_update_video_media_and_persist_it() {
            // Given
            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedResource = Fixture.Videos.resource(expectedType);
            final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));
            when(mediaResourceGateway.storeAudioVideo(any(), any()))
                    .thenReturn(expectedMedia);
            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

            // When
            final var actualOutput = uploadMediaUseCase.execute(aCommand);

            // Then
            assertEquals(expectedType, actualOutput.mediaType());
            assertEquals(expectedId.getValue(), actualOutput.videoId());
            verify(videoGateway, times(1)).findById(expectedId);
            verify(mediaResourceGateway, times(1)).storeAudioVideo(expectedId, expectedVideoResource);
            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(expectedMedia, actualVideo.getVideo())
                            && Objects.isNull(actualVideo.getTrailer())
                            && Objects.isNull(actualVideo.getBanner())
                            && Objects.isNull(actualVideo.getThumbnail())
                            && Objects.isNull(actualVideo.getThumbnailHalf())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_upload_Then_should_update_trailer_media_and_persist_it() {
            // Given
            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();
            final var expectedType = VideoMediaType.TRAILER;
            final var expectedResource = Fixture.Videos.resource(expectedType);
            final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
            final var expectedMedia = Fixture.Videos.audioVideo(expectedType);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));
            when(mediaResourceGateway.storeAudioVideo(any(), any()))
                    .thenReturn(expectedMedia);
            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

            // When
            final var actualOutput = uploadMediaUseCase.execute(aCommand);

            // Then
            assertEquals(expectedType, actualOutput.mediaType());
            assertEquals(expectedId.getValue(), actualOutput.videoId());
            verify(videoGateway, times(1)).findById(expectedId);
            verify(mediaResourceGateway, times(1)).storeAudioVideo(expectedId, expectedVideoResource);
            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(expectedMedia, actualVideo.getTrailer())
                            && Objects.isNull(actualVideo.getVideo())
                            && Objects.isNull(actualVideo.getBanner())
                            && Objects.isNull(actualVideo.getThumbnail())
                            && Objects.isNull(actualVideo.getThumbnailHalf())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_upload_Then_should_update_banner_media_and_persist_it() {
            // Given
            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();
            final var expectedType = VideoMediaType.BANNER;
            final var expectedResource = Fixture.Videos.resource(expectedType);
            final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
            final var expectedMedia = Fixture.Videos.image(expectedType);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));
            when(mediaResourceGateway.storeImage(any(), any()))
                    .thenReturn(expectedMedia);
            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

            // When
            final var actualOutput = uploadMediaUseCase.execute(aCommand);

            // Then
            assertEquals(expectedType, actualOutput.mediaType());
            assertEquals(expectedId.getValue(), actualOutput.videoId());
            verify(videoGateway, times(1)).findById(expectedId);
            verify(mediaResourceGateway, times(1)).storeImage(expectedId, expectedVideoResource);
            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(expectedMedia, actualVideo.getBanner())
                            && Objects.isNull(actualVideo.getVideo())
                            && Objects.isNull(actualVideo.getTrailer())
                            && Objects.isNull(actualVideo.getThumbnail())
                            && Objects.isNull(actualVideo.getThumbnailHalf())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_upload_Then_should_update_thumbnail_media_and_persist_it() {
            // Given
            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();
            final var expectedType = VideoMediaType.THUMBNAIL;
            final var expectedResource = Fixture.Videos.resource(expectedType);
            final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
            final var expectedMedia = Fixture.Videos.image(expectedType);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));
            when(mediaResourceGateway.storeImage(any(), any()))
                    .thenReturn(expectedMedia);
            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

            // When
            final var actualOutput = uploadMediaUseCase.execute(aCommand);

            // Then
            assertEquals(expectedType, actualOutput.mediaType());
            assertEquals(expectedId.getValue(), actualOutput.videoId());
            verify(videoGateway, times(1)).findById(expectedId);
            verify(mediaResourceGateway, times(1)).storeImage(expectedId, expectedVideoResource);
            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(expectedMedia, actualVideo.getThumbnail())
                            && Objects.isNull(actualVideo.getVideo())
                            && Objects.isNull(actualVideo.getTrailer())
                            && Objects.isNull(actualVideo.getBanner())
                            && Objects.isNull(actualVideo.getThumbnailHalf())
            ));
        }

        @Test
        void Given_a_valid_command_When_calls_upload_Then_should_update_thumbnail_half_media_and_persist_it() {
            // Given
            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();
            final var expectedType = VideoMediaType.THUMBNAIL_HALF;
            final var expectedResource = Fixture.Videos.resource(expectedType);
            final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);
            final var expectedMedia = Fixture.Videos.image(expectedType);

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(aVideo));
            when(mediaResourceGateway.storeImage(any(), any()))
                    .thenReturn(expectedMedia);
            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            final var aCommand =
                    UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

            // When
            final var actualOutput = uploadMediaUseCase.execute(aCommand);

            // Then
            assertEquals(expectedType, actualOutput.mediaType());
            assertEquals(expectedId.getValue(), actualOutput.videoId());
            verify(videoGateway, times(1)).findById(expectedId);
            verify(mediaResourceGateway, times(1)).storeImage(expectedId, expectedVideoResource);
            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(expectedMedia, actualVideo.getThumbnailHalf())
                            && Objects.isNull(actualVideo.getVideo())
                            && Objects.isNull(actualVideo.getTrailer())
                            && Objects.isNull(actualVideo.getBanner())
                            && Objects.isNull(actualVideo.getThumbnail())
            ));
        }
    }

    @Nested
    @DisplayName("Upload a resource with an invalid command")
    class UploadResourceWithInvalidCommand {

        @Test
        void Given_an_invalid_video_id_When_calls_upload_Then_should_return_not_found() {
            // Given
            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();
            final var expectedType = VideoMediaType.THUMBNAIL;
            final var expectedResource = Fixture.Videos.resource(expectedType);
            final var expectedVideoResource = VideoResource.with(expectedResource, expectedType);

            final var expectedErrorMessage = "Video with ID %s was not found"
                    .formatted(expectedId.getValue());

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.empty());

            final var aCommand =
                    UploadMediaCommand.with(expectedId.getValue(), expectedVideoResource);

            // When
            Executable invalidMethodCall = () -> uploadMediaUseCase.execute(aCommand);

            // Then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }
    }
}
