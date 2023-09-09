package com.fullcycle.admin.catalogo.application.video.delete;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.admin.catalogo.domain.video.MediaResourceGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DeleteVideoUseCaseTest extends UseCaseTest {

    public static final String GATEWAY_ERROR = "Gateway error";

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultDeleteVideoUseCase deleteVideoUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, mediaResourceGateway);
    }

    @Nested
    @DisplayName("Delete video with a valid identifier")
    class DeleteWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_delete_video_Then_should_delete_it() {
            // Given
            final var expectedId = VideoID.unique();

            doNothing()
                    .when(videoGateway).deleteById(Mockito.any());
            doNothing()
                    .when(mediaResourceGateway).clearResources(any());

            // When
            Executable validMethodCall = () -> deleteVideoUseCase.execute(expectedId.getValue());

            // Then
            assertDoesNotThrow(validMethodCall);
            verify(videoGateway).deleteById(expectedId);
            verify(mediaResourceGateway).clearResources(expectedId);
        }

        @Test
        void Given_a_valid_identifier_When_calls_delete_video_and_gateway_throws_exception_Then_should_receive_error() {
            // Given
            final var expectedId = VideoID.unique();

            doThrow(InternalErrorException.with(GATEWAY_ERROR, new RuntimeException()))
                    .when(videoGateway).deleteById(Mockito.any());

            // When
            Executable invalidMethodCall = () -> deleteVideoUseCase.execute(expectedId.getValue());

            // Then
            final var actualException = assertThrows(InternalErrorException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(GATEWAY_ERROR, actualException.getMessage());
        }
    }

    @Nested
    @DisplayName("Delete video with an invalid identifier")
    class DeleteWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_delete_video_Then_should_be_ok() {
            // Given
            final var expectedId = VideoID.from("an-invalid-id");

            doNothing()
                    .when(videoGateway).deleteById(Mockito.any());

            // When
            Executable validMethodCall = () -> deleteVideoUseCase.execute(expectedId.getValue());

            // Then
            assertDoesNotThrow(validMethodCall);
        }
    }

}
