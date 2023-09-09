package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.services.StorageService;
import com.fullcycle.admin.catalogo.infrastructure.services.local.InMemoryStorageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

import static com.fullcycle.admin.catalogo.domain.Fixture.Videos.mediaType;
import static com.fullcycle.admin.catalogo.domain.Fixture.Videos.resource;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
class DefaultMediaResourceGatewayTest {

    @Autowired
    private MediaResourceGateway mediaResourceGateway;

    @Autowired
    private StorageService storageService;

    @BeforeEach
    void setUp() {
        storageService().reset();
    }

    @Nested
    @DisplayName("Create with valid resource values")
    class CreateWithValidResourceValues {

        @Test
        void Given_a_valid_resource_When_calls_store_audio_video_Then_should_store_it() {
            // Given
            final var expectedVideoId = VideoID.unique();
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedResource = resource(expectedType);
            final var expectedLocation = "videoId-%s/type-%s"
                    .formatted(expectedVideoId.getValue(), expectedType.name());
            final var expectedStatus = MediaStatus.PENDING;
            final var expectedEncodedLocation = "";

            // When
            final var actualMedia = mediaResourceGateway.storeAudioVideo(expectedVideoId,
                    VideoResource.with(expectedResource, expectedType));

            // Then
            assertNotNull(actualMedia.id());
            assertEquals(expectedLocation, actualMedia.rawLocation());
            assertEquals(expectedEncodedLocation, actualMedia.encodedLocation());
            assertEquals(expectedResource.name(), actualMedia.name());
            assertEquals(expectedResource.checksum(), actualMedia.checksum());
            assertEquals(expectedStatus, actualMedia.status());

            final var actualStored = storageService().storage().get(expectedLocation);
            assertEquals(expectedResource, actualStored);
        }

        @Test
        void Given_a_valid_resource_When_calls_store_image_Then_should_store_it() {
            // Given
            final var expectedVideoId = VideoID.unique();
            final var expectedType = VideoMediaType.BANNER;
            final var expectedResource = resource(expectedType);
            final var expectedLocation = "videoId-%s/type-%s"
                    .formatted(expectedVideoId.getValue(), expectedType.name());

            // When
            final var actualMedia = mediaResourceGateway.storeImage(expectedVideoId,
                    VideoResource.with(expectedResource, expectedType));

            // Then
            assertNotNull(actualMedia.id());
            assertEquals(expectedLocation, actualMedia.location());
            assertEquals(expectedResource.name(), actualMedia.name());
            assertEquals(expectedResource.checksum(), actualMedia.checksum());

            final var actualStored = storageService().storage().get(expectedLocation);
            assertEquals(expectedResource, actualStored);
        }
    }

    @Nested
    @DisplayName("Delete resources with valid resource identifier")
    class DeleteWithValidResourceIdentifier {

        @Test
        void Given_a_valid_video_id_When_calls_clear_resources_Then_should_delete_all_its_resources() {
            // Given
            final var videoOne = VideoID.unique();
            final var videoTwo = VideoID.unique();

            final var toBeDeleted = new ArrayList<String>();
            toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.VIDEO.name()));
            toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.TRAILER.name()));
            toBeDeleted.add("videoId-%s/type-%s".formatted(videoOne.getValue(), VideoMediaType.BANNER.name()));

            final var expectedValues = new ArrayList<String>();
            expectedValues.add("videoId-%s/type-%s".formatted(videoTwo.getValue(), VideoMediaType.VIDEO.name()));
            expectedValues.add("videoId-%s/type-%s".formatted(videoTwo.getValue(), VideoMediaType.BANNER.name()));

            toBeDeleted.forEach(id -> storageService().store(id, resource(mediaType())));
            expectedValues.forEach(id -> storageService().store(id, resource(mediaType())));

            assertEquals(5, storageService().storage().size());

            // When
            mediaResourceGateway.clearResources(videoOne);

            // Then
            assertEquals(2, storageService().storage().size());
            final var actualKeys = storageService().storage().keySet();
            assertTrue(expectedValues.size() == actualKeys.size()
                    && actualKeys.containsAll(expectedValues));
        }
    }

    @Nested
    @DisplayName("Get resource with valid resource identifier")
    class GetWithValidResourceIdentifier {

        @Test
        void Given_a_valid_video_id_When_calls_get_resource_Then_should_return_it() {
            // Given
            final var videoOne = VideoID.unique();
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedResource = resource(expectedType);

            storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(),
                    expectedType.name()), expectedResource);
            storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(),
                    VideoMediaType.TRAILER.name()), resource(mediaType()));
            storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(),
                    VideoMediaType.BANNER.name()), resource(mediaType()));

            assertEquals(3, storageService().storage().size());

            // When & Then
            mediaResourceGateway.getResource(videoOne, expectedType)
                    .ifPresent(actualResource -> assertEquals(expectedResource, actualResource));
        }
    }

    @Nested
    @DisplayName("Get resource with invalid resource identifier")
    class GetWithInvalidResourceIdentifier {

        @Test
        void Given_an_invalid_type_When_calls_get_resource_Then_should_return_empty() {
            // Given
            final var videoOne = VideoID.unique();
            final var expectedType = VideoMediaType.THUMBNAIL;

            storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(),
                    VideoMediaType.VIDEO.name()), resource(mediaType()));
            storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(),
                    VideoMediaType.TRAILER.name()), resource(mediaType()));
            storageService().store("videoId-%s/type-%s".formatted(videoOne.getValue(),
                    VideoMediaType.BANNER.name()), resource(mediaType()));

            assertEquals(3, storageService().storage().size());

            // When
            final var actualResource = mediaResourceGateway.getResource(videoOne, expectedType);

            // Then
            assertTrue(actualResource.isEmpty());
        }
    }

    private InMemoryStorageService storageService() {
        return (InMemoryStorageService) storageService;
    }
}