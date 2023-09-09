package com.fullcycle.admin.catalogo.infrastructure.services.impl;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.VideoMediaType;
import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GCStorageServiceTest {

    private GCStorageService gcStorageService;

    private Storage storage;

    private String bucket = "fc3_test";

    @BeforeEach
    void setUp() {
        this.storage = mock(Storage.class);
        this.gcStorageService = new GCStorageService(this.bucket, this.storage);
    }

    @DisplayName("Store a resource with valid params")
    @Nested
    class StoreWithValidParams {

        @Test
        void Given_a_valid_resource_When_calls_store_Then_should_store_a_resource() {
            // Given
            final var expectedName = IdUtils.uuid();
            final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

            final var blob = mockBlob(expectedName, expectedResource);
            doReturn(blob)
                    .when(storage).create(any(BlobInfo.class), any());

            // When
            gcStorageService.store(expectedName, expectedResource);

            // Then
            final var captor = ArgumentCaptor.forClass(BlobInfo.class);

            verify(storage, times(1)).create(captor.capture(), any(byte[].class));
            final var actualBlob = captor.getValue();
            assertEquals(bucket, actualBlob.getBlobId().getBucket());
            assertEquals(expectedName, actualBlob.getBlobId().getName());
            assertEquals(expectedName, actualBlob.getName());
            assertEquals(expectedResource.checksum(), actualBlob.getCrc32cToHexString());
            assertEquals(expectedResource.contentType(), actualBlob.getContentType());
        }
    }

    @DisplayName("Get a resource with valid param values")
    @Nested
    class GetWithValidParamValues {

        @Test
        void Given_a_valid_name_When_calls_get_Then_should_return_a_resource() {
            // Given
            final var expectedName = IdUtils.uuid();
            final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

            final var blob = mockBlob(expectedName, expectedResource);
            doReturn(blob)
                    .when(storage).get(anyString(), anyString());

            // When & Then
            gcStorageService.get(expectedName)
                    .ifPresent(actualResource -> assertEquals(expectedResource, actualResource));

            verify(storage, times(1)).get(bucket, expectedName);
        }
    }

    @DisplayName("Get a resource with invalid param values")
    @Nested
    class GetWithInvalidParamValues {

        @Test
        void Given_an_invalid_name_When_calls_get_Then_should_return_empty() {
            // Given
            final var expectedName = IdUtils.uuid();
            final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

            final var blob = mockBlob(expectedName, expectedResource);
            doReturn(null)
                    .when(storage).get(anyString(), anyString());

            // When
            final var actualResource = gcStorageService.get(expectedName);

            // Then
            assertTrue(actualResource.isEmpty());
            verify(storage, times(1)).get(bucket, expectedName);
        }
    }

    @DisplayName("List resources with valid param values")
    @Nested
    class ListWithValidParamValues {

        @Test
        void Given_a_valid_prefix_When_calls_list_Then_should_retrieve_all() {
            // Given
            final var expectedPrefix = "media_";
            final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
            final var expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
            final var expectedNameBanner = IdUtils.uuid();
            final var expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);

            final var expectedResources = List.of(expectedNameBanner, expectedNameVideo);
            final var blobVideo = mockBlob(expectedNameVideo, expectedVideo);
            final var blobBanner = mockBlob(expectedNameBanner, expectedBanner);

            final var page = Mockito.mock(Page.class);
            doReturn(List.of(blobVideo, blobBanner))
                    .when(page).iterateAll();
            doReturn(page)
                    .when(storage).list(anyString(), any());

            // When
            final var actualResource = gcStorageService.list(expectedPrefix);

            // Then
            verify(storage, times(1)).list(bucket, Storage.BlobListOption.prefix(expectedPrefix));
            assertTrue(expectedResources.size() == actualResource.size()
                    && expectedResources.containsAll(actualResource));
        }
    }

    @DisplayName("Delete resources with valid param values")
    @Nested
    class DeleteWithValidParamValues {

        @Test
        void Given_valid_names_When_calls_delete_Then_should_delete_all() {
            final var expectedPrefix = "media_";

            final var expectedNameVideo = expectedPrefix + IdUtils.uuid();
            final var expectedNameBanner = expectedPrefix + IdUtils.uuid();

            final var expectedResources = List.of(expectedNameBanner, expectedNameVideo);

            // When
            gcStorageService.deleteAll(expectedResources);

            // Then
            final var captor = ArgumentCaptor.forClass(List.class);
            verify(storage, times(1)).delete(captor.capture());

            final var actualResources = ((List<BlobId>) captor.getValue()).stream()
                    .map(BlobId::getName)
                    .toList();
            assertTrue(expectedResources.size() == actualResources.size()
                    && expectedResources.containsAll(actualResources));
        }
    }

    private Blob mockBlob(final String name, final Resource aResource) {
        final var blob = mock(Blob.class);
        when(blob.getBlobId()).thenReturn(BlobId.of(this.bucket, name));
        when(blob.getCrc32cToHexString()).thenReturn(aResource.checksum());
        when(blob.getContent()).thenReturn(aResource.content());
        when(blob.getContentType()).thenReturn(aResource.contentType());
        when(blob.getName()).thenReturn(aResource.name());
        return blob;
    }
}