package com.fullcycle.admin.catalogo.application.media.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.fullcycle.admin.catalogo.application.video.media.get.DefaultGetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaCommand;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class GetMediaUseCaseTest extends UseCaseTest {

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultGetMediaUseCase getMediaUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(mediaResourceGateway);
    }

    @Nested
    @DisplayName("Get a media by video id with a valid identifier")
    class GetGenreByIdWithValidIdentifier {

        @Test
        void Given_a_valid_video_id_and_type_When_calls_get_media_Then_should_return_a_resource() {
            // Given
            final var expectedId = VideoID.unique();
            final var expectedType = Fixture.Videos.mediaType();
            final var expectedResource = Fixture.Videos.resource(expectedType);

            when(mediaResourceGateway.getResource(expectedId, expectedType))
                    .thenReturn(Optional.of(expectedResource));

            final var aCommand = GetMediaCommand
                    .with(expectedId.getValue(), expectedType.name());

            // When
            final var actualMedia = getMediaUseCase.execute(aCommand);

            // Then
            assertEquals(expectedResource.content(), actualMedia.content());
            assertEquals(expectedResource.contentType(), actualMedia.contentType());
            assertEquals(expectedResource.name(), actualMedia.name());
        }
    }

    @Nested
    @DisplayName("Get a media by video id with an invalid identifier")
    class GetGenreByIdWithInvalidIdentifier {

        @Test
        void Given_an_invalid_video_id_and_valid_type_When_calls_get_media_Then_should_return_not_found() {
            // Given
            final var expectedId = VideoID.unique();
            final var expectedType = Fixture.Videos.mediaType();
            final var expectedErrorMessage = "Resource %s not found for video %s"
                    .formatted(expectedType.name(), expectedId.getValue());

            when(mediaResourceGateway.getResource(expectedId, expectedType))
                    .thenReturn(Optional.empty());

            final var aCommand = GetMediaCommand.with(expectedId.getValue(), expectedType.name());

            // When
            Executable invalidMethodCall = () -> getMediaUseCase.execute(aCommand);

            // Then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }

        @Test
        void Given_a_valid_video_id_and_invalid_type_When_calls_get_media_Then_should_return_type_not_found() {
            // Given
            final var expectedId = VideoID.unique();
            final var anInvalidType = "an-invalid-type";
            final var expectedErrorMessage = "Media type %s does not exist."
                    .formatted(anInvalidType);

            final var aCommand = GetMediaCommand.with(expectedId.getValue(), anInvalidType);

            // When
            Executable invalidMethodCall = () -> getMediaUseCase.execute(aCommand);

            // Then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        }
    }
}
