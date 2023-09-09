package com.fullcycle.admin.catalogo.infrastructure.services.local;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InMemoryStorageServiceTest {

    private InMemoryStorageService inMemoryStorageService = new InMemoryStorageService();

    @BeforeEach
    void setUp() {
        this.inMemoryStorageService.reset();
    }

    @DisplayName("Store a resource with valid params")
    @Nested
    class StoreWithValidParams {

        @Test
        void Given_a_valid_resource_When_calls_store_Then_should_store_a_resource() {
            // Given
            final var expectedName = IdUtils.uuid();
            final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

            // When
            inMemoryStorageService.store(expectedName, expectedResource);

            // Then
            assertEquals(expectedResource, inMemoryStorageService.storage().get(expectedName));
        }
    }

    @DisplayName("Get a resource with valid params")
    @Nested
    class GetWithValidParams {

        @Test
        void Given_a_valid_name_When_calls_get_Then_should_return_a_resource() {
            // Given
            final var expectedName = IdUtils.uuid();
            final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

            inMemoryStorageService.storage().put(expectedName, expectedResource);

            // When & Then
            inMemoryStorageService.get(expectedName)
                    .ifPresent(actualResource -> assertEquals(expectedResource, actualResource));
        }
    }

    @DisplayName("Get a resource with invalid param values")
    @Nested
    class GetWithInvalidParamValues {

        @Test
        void Given_an_invalid_name_When_calls_get_Then_should_return_empty() {
            // Given
            final var expectedName = IdUtils.uuid();

            // When
            final var actualResource = inMemoryStorageService.get(expectedName);

            // Then
            assertTrue(actualResource.isEmpty());
        }
    }

    @DisplayName("List resources with valid param values")
    @Nested
    class ListWithValidParamValues {

        @Test
        void Given_a_valid_prefix_When_calls_list_Then_should_retrieve_all() {
            // Given
            final var expectedNames = List.of(
                    "video_" + IdUtils.uuid(),
                    "video_" + IdUtils.uuid(),
                    "video_" + IdUtils.uuid()
            );

            final var all = new ArrayList<>(expectedNames);
            all.add("image_" + IdUtils.uuid());
            all.add("image_" + IdUtils.uuid());

            all.forEach(name -> inMemoryStorageService.storage()
                    .put(name, Fixture.Videos.resource(VideoMediaType.VIDEO)));

            assertEquals(5, inMemoryStorageService.storage().size());

            // When
            final var actualResource = inMemoryStorageService.list("video");

            // Then
            assertTrue(expectedNames.size() == actualResource.size()
                    && expectedNames.containsAll(actualResource));
        }
    }

    @DisplayName("Delete resources with valid param values")
    @Nested
    class DeleteWithValidParamValues {

        @Test
        void Given_valid_names_When_calls_delete_Then_should_delete_all() {
            // Given
            final var videos = List.of(
                    "video_" + IdUtils.uuid(),
                    "video_" + IdUtils.uuid(),
                    "video_" + IdUtils.uuid()
            );

            final var expectedNames = Set.of(
                    "image_" + IdUtils.uuid(),
                    "image_" + IdUtils.uuid()
            );

            final var all = new ArrayList<>(videos);
            all.addAll(expectedNames);

            all.forEach(name -> inMemoryStorageService.storage()
                    .put(name, Fixture.Videos.resource(VideoMediaType.VIDEO)));

            assertEquals(5, inMemoryStorageService.storage().size());

            // When
            inMemoryStorageService.deleteAll(videos);

            // Then
            assertEquals(2, inMemoryStorageService.storage().size());
            assertTrue(expectedNames.size() == inMemoryStorageService.storage().size()
                    && expectedNames.containsAll(inMemoryStorageService.storage().keySet()));
        }
    }
}