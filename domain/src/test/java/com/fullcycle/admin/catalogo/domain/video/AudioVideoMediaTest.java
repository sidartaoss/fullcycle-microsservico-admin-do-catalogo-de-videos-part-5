package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AudioVideoMediaTest extends UnitTest {

    @DisplayName("Create an audio video with valid params")
    @Nested
    class CreateWithValidParams {

        @Test
        void Given_valid_params_When_calls_newAudioVideo_Then_should_instantiate_an_audio_video() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final var expectedRawLocation = "/images/ac";
            final var expectedEncodedLocation = "/images/ac-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            // When
            final var actualVideo = AudioVideoMedia
                    .with(expectedId,
                            expectedChecksum,
                            expectedName,
                            expectedRawLocation,
                            expectedEncodedLocation,
                            expectedStatus);

            // Then
            assertNotNull(actualVideo);
            assertEquals(expectedId, actualVideo.id());
            assertEquals(expectedChecksum, actualVideo.checksum());
            assertEquals(expectedName, actualVideo.name());
            assertEquals(expectedRawLocation, actualVideo.rawLocation());
            assertEquals(expectedEncodedLocation, actualVideo.encodedLocation());
            assertEquals(expectedStatus, actualVideo.status());
        }

        @Test
        void Given_two_images_with_different_ids_When_calls_equals_Then_should_return_not_the_same() {
            // Given
            final var expectedIdVideo1 = IdUtils.uuid();
            final var expectedIdVideo2 = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final var expectedRawLocation = "/images/ac";
            final var expectedEncodedLocation = "/images/ac-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var video1 =
                    AudioVideoMedia.with(expectedIdVideo1,
                            expectedChecksum,
                            "Random",
                            expectedRawLocation,
                            expectedEncodedLocation,
                            expectedStatus);
            final var video2 =
                    AudioVideoMedia.with(expectedIdVideo2,
                            expectedChecksum,
                            "Simple",
                            expectedRawLocation,
                            expectedEncodedLocation,
                            expectedStatus);

            // Then
            assertNotSame(video1, video2);
        }
    }

    @DisplayName("Create an audio video with invalid params")
    @Nested
    class CreateWithInvalidParams {

        @Test
        void Given_a_null_id_When_calls_with_Then_should_return_error() {
            // Given
            final String aNullId = null;
            final String expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final var expectedRawLocation = "/images/abc";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'id' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(aNullId,
                    expectedChecksum,
                    expectedName,
                    expectedRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_id_When_calls_with_Then_should_return_error() {
            // Given
            final String anEmptyId = " ";
            final String expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final var expectedRawLocation = "/images/abc";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'id' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(anEmptyId,
                    expectedChecksum,
                    expectedName,
                    expectedRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_a_null_checksum_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final String aNullChecksum = null;
            final var expectedName = "Banner.png";
            final var expectedRawLocation = "/images/abc";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'checksum' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    aNullChecksum,
                    expectedName,
                    expectedRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_checksum_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var anEmptyChecksum = " ";
            final var expectedName = "Banner.png";
            final var expectedRawLocation = "/images/abc";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'checksum' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    anEmptyChecksum,
                    expectedName,
                    expectedRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_a_null_name_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final String aNullName = null;
            final var expectedRawLocation = "/images/abc";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'name' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    expectedChecksum,
                    aNullName,
                    expectedRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_name_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final String anEmptyName = " ";
            final var expectedRawLocation = "/images/abc";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'name' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    expectedChecksum,
                    anEmptyName,
                    expectedRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_a_null_raw_location_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final String aNullRawLocation = null;
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'rawLocation' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    expectedChecksum,
                    expectedName,
                    aNullRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_raw_location_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final var anEmptyRawLocation = " ";
            final var expectedEncodedLocation = "/images/abc-encoded";
            final var expectedStatus = MediaStatus.PENDING;

            final var expectedErrorMessage = "'rawLocation' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    expectedChecksum,
                    expectedName,
                    anEmptyRawLocation,
                    expectedEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_a_null_encoded_location_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedId = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final String expectedRawLocation = "/images/abc";
            final String aNullEncodedLocation = null;
            final var expectedStatus = MediaStatus.PENDING;

            // When
            Executable invalidMethodCall = () -> AudioVideoMedia.with(expectedId,
                    expectedChecksum,
                    expectedName,
                    expectedRawLocation,
                    aNullEncodedLocation,
                    expectedStatus);

            // Then
            final var actualException = assertThrows(NullPointerException.class, invalidMethodCall);
        }
    }
}