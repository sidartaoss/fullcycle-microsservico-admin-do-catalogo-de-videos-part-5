package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ImageMediaTest extends UnitTest {

    @DisplayName("Create an image media with valid params")
    @Nested
    class CreateWithValidParams {

        @Test
        void Given_valid_params_When_calls_newImage_Then_should_instantiate_an_image_media() {
            // Given
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final var expectedLocation = "/images/ac";

            // When
            final var actualImage =
                    ImageMedia.with(expectedChecksum, expectedName, expectedLocation);

            // Then
            assertNotNull(actualImage);
            assertEquals(expectedChecksum, actualImage.checksum());
            assertEquals(expectedName, actualImage.name());
            assertEquals(expectedLocation, actualImage.location());
        }

        @Test
        void Given_two_images_with_different_ids_When_calls_equals_Then_should_not_return_the_same() {
            // Given
            final var expectedId1 = IdUtils.uuid();
            final var expectedId2 = IdUtils.uuid();
            final var expectedChecksum = "abc";
            final var expectedLocation = "/images/ac";

            final var img1 =
                    ImageMedia.with(expectedId1,
                            expectedChecksum, "Random", expectedLocation);
            final var img2 =
                    ImageMedia.with(expectedId2,
                            expectedChecksum, "Simple", expectedLocation);

            // Then
            assertNotSame(img1, img2);
        }
    }

    @DisplayName("Create an image media with invalid params")
    @Nested
    class CreateWithInvalidParams {

        @Test
        void Given_a_null_checksum_When_calls_with_Then_should_return_error() {
            // Given
            final String aNullChecksum = null;
            final var expectedName = "Banner.png";
            final var expectedLocation = "/images/abc";

            final var expectedErrorMessage = "'checksum' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> ImageMedia.with(aNullChecksum, expectedName, expectedLocation);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_checksum_When_calls_with_Then_should_return_error() {
            // Given
            final var anEmptyChecksum = " ";
            final var expectedName = "Banner.png";
            final var expectedLocation = "/images/abc";

            final var expectedErrorMessage = "'checksum' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> ImageMedia.with(anEmptyChecksum, expectedName, expectedLocation);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_a_null_name_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedChecksum = "abc";
            final String aNullName = null;
            final var expectedLocation = "/images/abc";

            final var expectedErrorMessage = "'name' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> ImageMedia.with(expectedChecksum, aNullName, expectedLocation);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_name_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedChecksum = "abc";
            final String anEmptyName = " ";
            final var expectedLocation = "/images/abc";

            final var expectedErrorMessage = "'name' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> ImageMedia.with(expectedChecksum, anEmptyName, expectedLocation);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_a_null_location_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final String aNullLocation = null;

            final var expectedErrorMessage = "'location' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> ImageMedia.with(expectedChecksum, expectedName, aNullLocation);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }

        @Test
        void Given_an_empty_location_When_calls_with_Then_should_return_error() {
            // Given
            final var expectedChecksum = "abc";
            final var expectedName = "Banner.png";
            final String anEmptyLocation = " ";

            final var expectedErrorMessage = "'location' should not be empty or null";
            // When
            Executable invalidMethodCall = () -> ImageMedia.with(expectedChecksum, expectedName, anEmptyLocation);

            // Then
            final var actualException = assertThrows(IllegalArgumentException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }
    }
}