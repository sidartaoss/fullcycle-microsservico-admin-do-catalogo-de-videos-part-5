package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ApiTest;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.create.DefaultCreateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.delete.DefaultDeleteVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaOutput;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.DefaultGetVideoByIdUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdOutput;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.DefaultListVideosUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.VideoListOutput;
import com.fullcycle.admin.catalogo.application.video.update.DefaultUpdateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoOutput;
import com.fullcycle.admin.catalogo.application.video.upload.UploadMediaCommand;
import com.fullcycle.admin.catalogo.application.video.upload.UploadMediaOutput;
import com.fullcycle.admin.catalogo.application.video.upload.UploadMediaUseCase;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ControllerTest(controllers = VideoAPI.class)
class VideoAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateVideoUseCase createVideoUseCase;

    @MockBean
    private DefaultGetVideoByIdUseCase getVideoByIdUseCase;

    @MockBean
    private DefaultUpdateVideoUseCase updateVideoUseCase;

    @MockBean
    private DefaultDeleteVideoUseCase deleteVideoUseCase;

    @MockBean
    private DefaultListVideosUseCase listVideosUseCase;

    @MockBean
    private GetMediaUseCase getMediaUseCase;

    @MockBean
    private UploadMediaUseCase uploadMediaUseCase;

    @Nested
    @DisplayName("Create a video with valid request")
    class CreateWithValidRequest {

        @Test
        void Given_a_valid_request_When_calls_create_video_full_Then_should_return_its_identifier()
                throws Exception {
            // Given
            final var wesley = Fixture.CastMembers.wesley();
            final var tech = Fixture.Genres.tech();
            final var aulas = Fixture.Categories.aulas();

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedYearLaunched = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedOpened = Fixture.bool();
            final var expectedPublished = Fixture.bool();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(aulas.getId().getValue());
            final var expectedGenres = Set.of(tech.getId().getValue());
            final var expectedCastMembers = Set.of(wesley.getId().getValue());

            final var expectedVideo = new MockMultipartFile("video_file", "video.mp4",
                    "video/mp4", "video".getBytes());

            final var expectedTrailer = new MockMultipartFile("trailer_file", "trailer.mp4",
                    "video/mp4", "trailer".getBytes());

            final var expectedBanner = new MockMultipartFile("banner_file", "banner.jpg",
                    "image/jpg", "banner".getBytes());

            final var expectedThumbnail = new MockMultipartFile("thumbnail_file", "thumbnail.jpg",
                    "image/jpg", "thumbnail".getBytes());

            final var expectedThumbnailHalf = new MockMultipartFile("thumbnail_half_file",
                    "thumbnail_half.jpg", "image/jpg", "thumbnail_half".getBytes());

            final var expectedId = VideoID.unique();

            when(createVideoUseCase.execute(any()))
                    .thenReturn(CreateVideoOutput.from(expectedId));

            final var mockMvcRequest = multipart("/videos")
                    .file(expectedVideo)
                    .file(expectedTrailer)
                    .file(expectedBanner)
                    .file(expectedThumbnail)
                    .file(expectedThumbnailHalf)
                    .param("title", expectedTitle)
                    .param("description", expectedDescription)
                    .param("year_launched", expectedYearLaunched.toString())
                    .param("duration", expectedDuration.toString())
                    .param("opened", String.valueOf(expectedOpened))
                    .param("published", String.valueOf(expectedPublished))
                    .param("rating", expectedRating.getName())
                    .param("cast_members_id", wesley.getId().getValue())
                    .param("categories_id", aulas.getId().getValue())
                    .param("genres_id", tech.getId().getValue())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(ApiTest.VIDEOS_JWT);

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

            final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

            verify(createVideoUseCase, times(1)).execute(cmdCaptor.capture());
            final var actualCommand = cmdCaptor.getValue();

            assertEquals(expectedTitle, actualCommand.title());
            assertEquals(expectedDescription, actualCommand.description());
            assertEquals(expectedYearLaunched, actualCommand.launchedAt());
            assertEquals(expectedDuration, actualCommand.duration());
            assertEquals(expectedOpened, openedOf(actualCommand.releaseStatus()));
            assertEquals(expectedPublished, publishedOf(actualCommand.publishingStatus()));
            assertEquals(expectedRating, Rating.of(actualCommand.rating()).orElse(null));
            assertEquals(expectedCategories, actualCommand.categories());
            assertEquals(expectedGenres, actualCommand.genres());
            assertEquals(expectedCastMembers, actualCommand.castMembers());
            assertNotNull(actualCommand.video());
            assertEquals(expectedVideo.getOriginalFilename(), actualCommand.video().name());
            assertNotNull(actualCommand.trailer());
            assertEquals(expectedTrailer.getOriginalFilename(), actualCommand.trailer().name());
            assertNotNull(actualCommand.banner());
            assertEquals(expectedBanner.getOriginalFilename(), actualCommand.banner().name());
            assertNotNull(actualCommand.thumbnail());
            assertEquals(expectedThumbnail.getOriginalFilename(), actualCommand.thumbnail().name());
            assertNotNull(actualCommand.thumbnailHalf());
            assertEquals(expectedThumbnailHalf.getOriginalFilename(), actualCommand.thumbnailHalf().name());
        }

        @Test
        void Given_a_valid_request_When_calls_create_video_partial_Then_should_return_its_identifier()
                throws Exception {
            // Given
            final var wesley = Fixture.CastMembers.wesley();
            final var tech = Fixture.Genres.tech();
            final var aulas = Fixture.Categories.aulas();

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedYearLaunched = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedOpened = Fixture.bool();
            final var expectedPublished = Fixture.bool();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(aulas.getId().getValue());
            final var expectedGenres = Set.of(tech.getId().getValue());
            final var expectedCastMembers = Set.of(wesley.getId().getValue());

            final var expectedId = VideoID.unique();

            final var aRequest =
                    new CreateVideoRequest(
                            expectedTitle,
                            expectedDescription,
                            expectedDuration,
                            expectedYearLaunched,
                            expectedOpened,
                            expectedPublished,
                            expectedRating.getName(),
                            expectedCastMembers,
                            expectedCategories,
                            expectedGenres
                    );

            when(createVideoUseCase.execute(any()))
                    .thenReturn(CreateVideoOutput.from(expectedId));

            final var mockMvcRequest = post("/videos")
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsBytes(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/videos/" + expectedId.getValue()))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

            final var cmdCaptor = ArgumentCaptor.forClass(CreateVideoCommand.class);

            verify(createVideoUseCase, times(1)).execute(cmdCaptor.capture());
            final var actualCommand = cmdCaptor.getValue();

            assertEquals(expectedTitle, actualCommand.title());
            assertEquals(expectedDescription, actualCommand.description());
            assertEquals(expectedYearLaunched, actualCommand.launchedAt());
            assertEquals(expectedDuration, actualCommand.duration());
            assertEquals(expectedOpened, openedOf(actualCommand.releaseStatus()));
            assertEquals(expectedPublished, publishedOf(actualCommand.publishingStatus()));
            assertEquals(expectedRating, Rating.of(actualCommand.rating()).orElse(null));
            assertEquals(expectedCategories, actualCommand.categories());
            assertEquals(expectedGenres, actualCommand.genres());
            assertEquals(expectedCastMembers, actualCommand.castMembers());
            assertNull(actualCommand.video());
            assertNull(actualCommand.trailer());
            assertNull(actualCommand.banner());
            assertNull(actualCommand.thumbnail());
            assertNull(actualCommand.thumbnailHalf());
        }
    }

    @Nested
    @DisplayName("Create a video with invalid request")
    class CreateWithInvalidRequest {

        @Test
        void Given_an_invalid_request_When_calls_create_video_full_Then_should_return_error()
                throws Exception {
            // Given
            final var expectedErrorMessage = "'title' should not be null";
            when(createVideoUseCase.execute(any()))
                    .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

            final var mockMvcRequest = multipart("/videos")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(ApiTest.VIDEOS_JWT);

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        }

        @Test
        void Given_an_invalid_request_When_calls_create_video_partial_Then_should_return_error()
                throws Exception {
            // Given
            final var expectedErrorMessage = "'title' should not be null";
            when(createVideoUseCase.execute(any()))
                    .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

            final var mockMvcRequest = post("/videos")
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "": ""
                            }
                            """);

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        }

        @Test
        void Given_an_invalid_request_body_When_calls_create_video_partial_Then_should_return_error()
                throws Exception {
            // Given
            final var expectedErrorMessage = "'title' should not be null";

            final var mockMvcRequest = post("/videos")
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get a video with valid identifier")
    class GetVideoWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_get_by_id_Then_should_return_video() throws Exception {
            // Given
            final var wesley = Fixture.CastMembers.wesley();
            final var tech = Fixture.Genres.tech();
            final var aulas = Fixture.Categories.aulas();

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedYearLaunched = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedOpened = Fixture.bool();
            final var expectedPublished = Fixture.bool();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(aulas.getId().getValue());
            final var expectedGenres = Set.of(tech.getId().getValue());
            final var expectedCastMembers = Set.of(wesley.getId().getValue());

            final var expectedVideo = Fixture.Videos.audioVideo(VideoMediaType.VIDEO);
            final var expectedTrailer = Fixture.Videos.audioVideo(VideoMediaType.TRAILER);

            final var expectedBanner = Fixture.Videos.image(VideoMediaType.BANNER);
            final var expectedThumbnail = Fixture.Videos.image(VideoMediaType.THUMBNAIL);
            final var expectedThumbnailHalf = Fixture.Videos.image(VideoMediaType.THUMBNAIL_HALF);

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    Year.of(expectedYearLaunched),
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(releaseStatusOf(expectedOpened))
                    .publishingStatus(publishingStatusOf(expectedPublished))
                    .categories(mapTo(expectedCategories, CategoryID::from))
                    .genres(mapTo(expectedGenres, GenreID::from))
                    .castMembers(mapTo(expectedCastMembers, CastMemberID::from))
                    .video(expectedVideo)
                    .trailer(expectedTrailer)
                    .banner(expectedBanner)
                    .thumbnail(expectedThumbnail)
                    .thumbnailHalf(expectedThumbnailHalf)
                    .createdAt(InstantUtils.now())
                    .updatedAt(InstantUtils.now());

            final var aVideo = Video.newVideo(aBuilder);
            final var expectedId = aVideo.getId().getValue();

            when(getVideoByIdUseCase.execute(any()))
                    .thenReturn(GetVideoByIdOutput.from(aVideo));

            final var request = get("/videos/{id}", expectedId)
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)))
                    .andExpect(jsonPath("$.title", equalTo(expectedTitle)))
                    .andExpect(jsonPath("$.description", equalTo(expectedDescription)))
                    .andExpect(jsonPath("$.year_launched", equalTo(expectedYearLaunched)))
                    .andExpect(jsonPath("$.duration", equalTo(expectedDuration)))
                    .andExpect(jsonPath("$.opened", equalTo(expectedOpened)))
                    .andExpect(jsonPath("$.rating", equalTo(expectedRating.getName())))
                    .andExpect(jsonPath("$.created_at", equalTo(aVideo.getCreatedAt().toString())))
                    .andExpect(jsonPath("$.updated_at", equalTo(aVideo.getUpdatedAt().toString())))

                    .andExpect(jsonPath("$.banner.id", equalTo(expectedBanner.id())))
                    .andExpect(jsonPath("$.banner.name", equalTo(expectedBanner.name())))
                    .andExpect(jsonPath("$.banner.location", equalTo(expectedBanner.location())))
                    .andExpect(jsonPath("$.banner.checksum", equalTo(expectedBanner.checksum())))

                    .andExpect(jsonPath("$.thumbnail.id", equalTo(expectedThumbnail.id())))
                    .andExpect(jsonPath("$.thumbnail.name", equalTo(expectedThumbnail.name())))
                    .andExpect(jsonPath("$.thumbnail.location", equalTo(expectedThumbnail.location())))
                    .andExpect(jsonPath("$.thumbnail.checksum", equalTo(expectedThumbnail.checksum())))

                    .andExpect(jsonPath("$.thumbnail_half.id", equalTo(expectedThumbnailHalf.id())))
                    .andExpect(jsonPath("$.thumbnail_half.name", equalTo(expectedThumbnailHalf.name())))
                    .andExpect(jsonPath("$.thumbnail_half.location", equalTo(expectedThumbnailHalf.location())))
                    .andExpect(jsonPath("$.thumbnail_half.checksum", equalTo(expectedThumbnailHalf.checksum())))

                    .andExpect(jsonPath("$.video.id", equalTo(expectedVideo.id())))
                    .andExpect(jsonPath("$.video.name", equalTo(expectedVideo.name())))
                    .andExpect(jsonPath("$.video.checksum", equalTo(expectedVideo.checksum())))
                    .andExpect(jsonPath("$.video.location", equalTo(expectedVideo.rawLocation())))
                    .andExpect(jsonPath("$.video.encoded_location", equalTo(expectedVideo.encodedLocation())))
                    .andExpect(jsonPath("$.video.status", equalTo(expectedVideo.status().name())))

                    .andExpect(jsonPath("$.trailer.id", equalTo(expectedTrailer.id())))
                    .andExpect(jsonPath("$.trailer.name", equalTo(expectedTrailer.name())))
                    .andExpect(jsonPath("$.trailer.checksum", equalTo(expectedTrailer.checksum())))
                    .andExpect(jsonPath("$.trailer.location", equalTo(expectedTrailer.rawLocation())))
                    .andExpect(jsonPath("$.trailer.encoded_location", equalTo(expectedTrailer.encodedLocation())))
                    .andExpect(jsonPath("$.trailer.status", equalTo(expectedTrailer.status().name())))

                    .andExpect(jsonPath("$.categories_id", equalTo(new ArrayList(expectedCategories))))
                    .andExpect(jsonPath("$.genres_id", equalTo(new ArrayList(expectedGenres))))
                    .andExpect(jsonPath("$.cast_members_id", equalTo(new ArrayList(expectedCastMembers))));
            verify(getVideoByIdUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("Get a video with invalid identifier")
    class GetVideoWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_get_by_id_Then_should_return_not_found_exception()
                throws Exception {
            // Given
            final var expectedId = VideoID.unique();
            final var expectedErrorMessage = "Video with ID %s was not found"
                    .formatted(expectedId.getValue());

            when(getVideoByIdUseCase.execute(any()))
                    .thenThrow(NotFoundException.with(Video.class, expectedId));

            final var request = get("/videos/{id}", expectedId)
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        }
    }

    @Nested
    @DisplayName("Update a video with valid request")
    class UpdateWithValidRequest {

        @Test
        void Given_a_valid_request_When_calls_update_video_Then_should_return_video_id()
                throws Exception {
            // Given
            final var wesley = Fixture.CastMembers.wesley();
            final var tech = Fixture.Genres.tech();
            final var aulas = Fixture.Categories.aulas();

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedYearLaunched = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedOpened = Fixture.bool();
            final var expectedPublished = Fixture.bool();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(aulas.getId().getValue());
            final var expectedGenres = Set.of(tech.getId().getValue());
            final var expectedCastMembers = Set.of(wesley.getId().getValue());

            final var expectedId = VideoID.unique();

            final var aRequest =
                    new UpdateVideoRequest(
                            expectedTitle,
                            expectedDescription,
                            expectedDuration,
                            expectedYearLaunched,
                            expectedOpened,
                            expectedPublished,
                            expectedRating.getName(),
                            expectedCastMembers,
                            expectedCategories,
                            expectedGenres
                    );

            when(updateVideoUseCase.execute(any()))
                    .thenReturn(UpdateVideoOutput.from(expectedId));

            final var mockMvcRequest = put("/videos/{id}", expectedId.getValue())
                    .with(ApiTest.VIDEOS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));

            final var cmdCaptor = ArgumentCaptor.forClass(UpdateVideoCommand.class);

            verify(updateVideoUseCase, times(1)).execute(cmdCaptor.capture());
            final var actualCommand = cmdCaptor.getValue();

            assertEquals(expectedTitle, actualCommand.title());
            assertEquals(expectedDescription, actualCommand.description());
            assertEquals(expectedYearLaunched, actualCommand.launchedAt());
            assertEquals(expectedDuration, actualCommand.duration());
            assertEquals(expectedOpened, openedOf(actualCommand.releaseStatus()));
            assertEquals(expectedPublished, publishedOf(actualCommand.publishingStatus()));
            assertEquals(expectedRating, Rating.of(actualCommand.rating()).orElse(null));
            assertEquals(expectedCategories, actualCommand.categories());
            assertEquals(expectedGenres, actualCommand.genres());
            assertEquals(expectedCastMembers, actualCommand.castMembers());
            assertNull(actualCommand.video());
            assertNull(actualCommand.trailer());
            assertNull(actualCommand.banner());
            assertNull(actualCommand.thumbnail());
            assertNull(actualCommand.thumbnailHalf());
        }
    }

    @Nested
    @DisplayName("Update a video with invalid request")
    class UpdateWithInvalidRequest {

        @Test
        void Given_an_invalid_name_When_calls_update_video_Then_should_return_not_found_exception()
                throws Exception {
            // Given
            final var wesley = Fixture.CastMembers.wesley();
            final var tech = Fixture.Genres.tech();
            final var aulas = Fixture.Categories.aulas();

            final var anInvalidEmptyTitle = " ";
            final var expectedDescription = Fixture.Videos.description();
            final var expectedYearLaunched = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedOpened = Fixture.bool();
            final var expectedPublished = Fixture.bool();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(aulas.getId().getValue());
            final var expectedGenres = Set.of(tech.getId().getValue());
            final var expectedCastMembers = Set.of(wesley.getId().getValue());

            final var expectedId = VideoID.unique();

            final var aRequest =
                    new UpdateVideoRequest(
                            anInvalidEmptyTitle,
                            expectedDescription,
                            expectedDuration,
                            expectedYearLaunched,
                            expectedOpened,
                            expectedPublished,
                            expectedRating.getName(),
                            expectedCastMembers,
                            expectedCategories,
                            expectedGenres
                    );

            final var expectedErrorMessage = "'title' should not be empty";
            final var expectedErrorCount = 1;

            when(updateVideoUseCase.execute(any()))
                    .thenThrow(NotificationException.with(new Error(expectedErrorMessage)));

            final var request = put("/videos/{id}", expectedId)
                    .with(ApiTest.VIDEOS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)))
                    .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

            verify(updateVideoUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("Delete a video with valid identifier")
    class DeleteVideoWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_delete_video_Then_should_return_no_content() throws Exception {
            // Given
            final var expectedId = VideoID.unique();

            doNothing().when(deleteVideoUseCase)
                    .execute(any());

            final var request = delete("/videos/{id}", expectedId)
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNoContent());
            verify(deleteVideoUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("List videos with valid params")
    class ListVideosWithValidParams {

        @Test
        void Given_a_valid_params_When_calls_list_videos_Then_should_return_pagination()
                throws Exception {
            // Given
            final var expectedPage = 0;
            final var expectedPerPage = 0;
            final var expectedTerms = "Algo";
            final var expectedSort = "title";
            final var expectedDirection = "desc";
            final var expectedCastMembers = "cast1";
            final var expectedGenres = "gen1";
            final var expectedCategories = "cat1";

            final var expectedItemsCount = 1;
            final var expectedTotal = 1;

            final var aVideo = VideoPreview.from(Fixture.video());
            final var expectedItems = List.of(VideoListOutput.from(aVideo));

            when(listVideosUseCase.execute(any()))
                    .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

            final var request = get("/videos")
                    .queryParam("page", String.valueOf(expectedPage))
                    .queryParam("perPage", String.valueOf(expectedPerPage))
                    .queryParam("sort", expectedSort)
                    .queryParam("dir", expectedDirection)
                    .queryParam("search", expectedTerms)
                    .queryParam("cast_members_ids", expectedCastMembers)
                    .queryParam("categories_ids", expectedCategories)
                    .queryParam("genres_ids", expectedGenres)
                    .accept(MediaType.APPLICATION_JSON)
                    .with(ApiTest.VIDEOS_JWT);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                    .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                    .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                    .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                    .andExpect(jsonPath("$.items[0].id", is(equalTo(aVideo.id()))))
                    .andExpect(jsonPath("$.items[0].title", is(equalTo(aVideo.title()))))
                    .andExpect(jsonPath("$.items[0].description", is(equalTo(aVideo.description()))))
                    .andExpect(jsonPath("$.items[0].created_at", is(equalTo(aVideo.createdAt().toString()))))
                    .andExpect(jsonPath("$.items[0].updated_at", is(equalTo(aVideo.updatedAt().toString()))));

            final var cmdCaptor = ArgumentCaptor.forClass(VideoSearchQuery.class);

            verify(listVideosUseCase, times(1)).execute(cmdCaptor.capture());
            final var actualQuery = cmdCaptor.getValue();
            assertEquals(expectedPage, actualQuery.page());
            assertEquals(expectedPerPage, actualQuery.perPage());
            assertEquals(expectedDirection, actualQuery.direction());
            assertEquals(expectedSort, actualQuery.sort());
            assertEquals(expectedTerms, actualQuery.terms());
            assertEquals(Set.of(CategoryID.from(expectedCategories)), actualQuery.categories());
            assertEquals(Set.of(GenreID.from(expectedGenres)), actualQuery.genres());
            assertEquals(Set.of(CastMemberID.from(expectedCastMembers)), actualQuery.castMembers());
        }

        @Test
        void Given_a_valid_empty_params_When_calls_list_videos_with_default_values_Then_should_return_pagination()
                throws Exception {
            // Given
            final var expectedPage = 0;
            final var expectedPerPage = 25;
            final var expectedTerms = "";
            final var expectedSort = "title";
            final var expectedDirection = "asc";
            final var expectedCastMembers = "";
            final var expectedGenres = "";
            final var expectedCategories = "";

            final var expectedItemsCount = 1;
            final var expectedTotal = 1;

            final var aVideo = VideoPreview.from(Fixture.video());
            final var expectedItems = List.of(VideoListOutput.from(aVideo));

            when(listVideosUseCase.execute(any()))
                    .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

            final var request = get("/videos")
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                    .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                    .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                    .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                    .andExpect(jsonPath("$.items[0].id", is(equalTo(aVideo.id()))))
                    .andExpect(jsonPath("$.items[0].title", is(equalTo(aVideo.title()))))
                    .andExpect(jsonPath("$.items[0].description", is(equalTo(aVideo.description()))))
                    .andExpect(jsonPath("$.items[0].created_at", is(equalTo(aVideo.createdAt().toString()))))
                    .andExpect(jsonPath("$.items[0].updated_at", is(equalTo(aVideo.updatedAt().toString()))));

            final var cmdCaptor = ArgumentCaptor.forClass(VideoSearchQuery.class);

            verify(listVideosUseCase, times(1)).execute(cmdCaptor.capture());
            final var actualQuery = cmdCaptor.getValue();
            assertEquals(expectedPage, actualQuery.page());
            assertEquals(expectedPerPage, actualQuery.perPage());
            assertEquals(expectedDirection, actualQuery.direction());
            assertEquals(expectedSort, actualQuery.sort());
            assertEquals(expectedTerms, actualQuery.terms());
            assertTrue(actualQuery.categories().isEmpty());
            assertTrue(actualQuery.genres().isEmpty());
            assertTrue(actualQuery.castMembers().isEmpty());
        }
    }

    @Nested
    @DisplayName("Get a video media with valid identifier")
    class GetVideoMediaWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_and_file_type_When_calls_get_media_by_id_Then_should_return_video_media()
                throws Exception {
            // Given
            final var expectedId = VideoID.unique();
            final var expectedMediaType = VideoMediaType.VIDEO;
            final var expectedResource = Fixture.Videos.resource(expectedMediaType);

            final var expectedMedia = GetMediaOutput.from(expectedResource);
            when(getMediaUseCase.execute(any()))
                    .thenReturn(expectedMedia);

            final var request = get("/videos/{id}/medias/{type}",
                    expectedId.getValue(), expectedMediaType.name())
                    .with(ApiTest.VIDEOS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string(CONTENT_TYPE, expectedMedia.contentType()))
                    .andExpect(header().string(CONTENT_DISPOSITION,
                            "attachment; filename=%s".formatted(expectedMedia.name())))
                    .andExpect(header().string(CONTENT_LENGTH,
                            String.valueOf(expectedMedia.content().length)))
                    .andExpect(content().bytes(expectedMedia.content()));

            final var captor = ArgumentCaptor.forClass(GetMediaCommand.class);
            verify(getMediaUseCase, times(1)).execute(captor.capture());
            final var actualCommand = captor.getValue();
            assertEquals(expectedId.getValue(), actualCommand.videoId());
            assertEquals(expectedMediaType.name(), actualCommand.mediaType());
        }
    }

    @Nested
    @DisplayName("Upload a video media with valid identifier")
    class UploadVideoMediaWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_and_file_When_calls_upload_media_Then_should_store_it()
                throws Exception {
            // Given
            final var expectedId = VideoID.unique();
            final var expectedType = VideoMediaType.VIDEO;
            final var expectedResource = Fixture.Videos.resource(expectedType);

            final var expectedVideo = new MockMultipartFile("media_file", expectedResource.name(),
                    expectedResource.contentType(), expectedResource.content());

            when(uploadMediaUseCase.execute(any()))
                    .thenReturn(UploadMediaOutput.from(expectedId.getValue(), expectedType));

            final var request = multipart("/videos/{id}/medias/{type}",
                    expectedId.getValue(), expectedType.name())
                    .file(expectedVideo)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(ApiTest.VIDEOS_JWT);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isCreated())
                    .andExpect(header().string(LOCATION, "/videos/%s/medias/%s"
                            .formatted(expectedId.getValue(), expectedType.name())))
                    .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.video_id", equalTo(expectedId.getValue())))
                    .andExpect(jsonPath("$.media_type", equalTo(expectedType.name())));

            final var captor = ArgumentCaptor.forClass(UploadMediaCommand.class);
            verify(uploadMediaUseCase, times(1)).execute(captor.capture());
            final var actualCommand = captor.getValue();
            assertEquals(expectedId.getValue(), actualCommand.videoId());
            assertEquals(expectedResource.content(), actualCommand.videoResource().resource().content());
            assertEquals(expectedResource.name(), actualCommand.videoResource().resource().name());
            assertEquals(expectedResource.contentType(), actualCommand.videoResource().resource().contentType());
            assertEquals(expectedType, actualCommand.videoResource().type());
        }
    }

    @Nested
    @DisplayName("Upload a video media with invalid identifier")
    class UploadVideoMediaWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_and_file_When_calls_upload_media_Then_should_return_error()
                throws Exception {
            // Given
            final var expectedId = VideoID.unique();
            final var expectedResource = Fixture.Videos.resource(VideoMediaType.VIDEO);

            final var expectedVideo = new MockMultipartFile("media_file", expectedResource.name(),
                    expectedResource.contentType(), expectedResource.content());

            final var request = multipart("/videos/{id}/medias/INVALID",
                    expectedId.getValue())
                    .file(expectedVideo)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .with(ApiTest.VIDEOS_JWT);

            final var expectedErrorMessage = "Invalid INVALID for VideoMediaType";

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        }
    }

    private boolean openedOf(final ReleaseStatus aReleaseStatus) {
        return ReleaseStatus.RELEASED == aReleaseStatus;
    }

    private boolean publishedOf(final PublishingStatus aPublishingStatus) {
        return PublishingStatus.PUBLISHED == aPublishingStatus;
    }

    private PublishingStatus publishingStatusOf(Boolean aPublished) {
        return aPublished == Boolean.TRUE ? PublishingStatus.PUBLISHED : PublishingStatus.NOT_PUBLISHED;
    }

    private ReleaseStatus releaseStatusOf(Boolean anOpened) {
        return anOpened == Boolean.TRUE ? ReleaseStatus.RELEASED : ReleaseStatus.NOT_RELEASED;
    }
}