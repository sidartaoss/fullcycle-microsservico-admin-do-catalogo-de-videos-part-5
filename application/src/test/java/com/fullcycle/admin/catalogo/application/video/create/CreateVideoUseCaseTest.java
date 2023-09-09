package com.fullcycle.admin.catalogo.application.video.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.domain.exceptions.InternalErrorException;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CreateVideoUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @Mock
    private CategoryGateway categoryGateway;

    @Mock
    private GenreGateway genreGateway;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Mock
    private MediaResourceGateway mediaResourceGateway;

    @InjectMocks
    private DefaultCreateVideoUseCase createVideoUseCase;


    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, categoryGateway, genreGateway, castMemberGateway, mediaResourceGateway);
    }

    @Nested
    @DisplayName("Create a video with valid command values")
    class CreateWithValidCommandValues {

        @Test
        void Given_a_valid_command_When_calls_create_video_Then_should_return_a_video_id() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(),
                    Fixture.CastMembers.gabriel().getId()
            );

            final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
            final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
            final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
            final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
            final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.create(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final CreateVideoOutput actualOutput = createVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).create(argThat(aVideo ->
                    Objects.equals(actualOutput.id(), aVideo.getId().getValue())
                            && Objects.equals(expectedTitle, aVideo.getTitle())
                            && Objects.equals(expectedDescription, aVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, aVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, aVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, aVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, aVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, aVideo.getRating())
                            && Objects.equals(expectedCategories, aVideo.getCategories())
                            && Objects.equals(expectedGenres, aVideo.getGenres())
                            && Objects.equals(expectedCastMembers, aVideo.getCastMembers())
                            && Objects.nonNull(aVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), aVideo.getVideo().name())
                            && Objects.nonNull(aVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().name())
                            && Objects.nonNull(aVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), aVideo.getBanner().name())
                            && Objects.nonNull(aVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), aVideo.getThumbnail().name())
                            && Objects.nonNull(aVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), aVideo.getThumbnailHalf().name())
            ));
        }

        @Test
        void Given_a_valid_command_without_categories_When_calls_create_video_Then_should_return_a_video_id() {
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(),
                    Fixture.CastMembers.gabriel().getId()
            );

            final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
            final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
            final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
            final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
            final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.create(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final CreateVideoOutput actualOutput = createVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).create(argThat(aVideo ->
                    Objects.equals(actualOutput.id(), aVideo.getId().getValue())
                            && Objects.equals(expectedTitle, aVideo.getTitle())
                            && Objects.equals(expectedDescription, aVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, aVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, aVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, aVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, aVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, aVideo.getRating())
                            && Objects.equals(expectedCategories, aVideo.getCategories())
                            && Objects.equals(expectedGenres, aVideo.getGenres())
                            && Objects.equals(expectedCastMembers, aVideo.getCastMembers())
                            && Objects.nonNull(aVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), aVideo.getVideo().name())
                            && Objects.nonNull(aVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().name())
                            && Objects.nonNull(aVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), aVideo.getBanner().name())
                            && Objects.nonNull(aVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), aVideo.getThumbnail().name())
                            && Objects.nonNull(aVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), aVideo.getThumbnailHalf().name())
            ));
        }

        @Test
        void Given_a_valid_command_without_genres_When_calls_create_video_Then_should_return_a_video_id() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(),
                    Fixture.CastMembers.gabriel().getId()
            );

            final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
            final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
            final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
            final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
            final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.create(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final CreateVideoOutput actualOutput = createVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).create(argThat(aVideo ->
                    Objects.equals(actualOutput.id(), aVideo.getId().getValue())
                            && Objects.equals(expectedTitle, aVideo.getTitle())
                            && Objects.equals(expectedDescription, aVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, aVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, aVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, aVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, aVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, aVideo.getRating())
                            && Objects.equals(expectedCategories, aVideo.getCategories())
                            && Objects.equals(expectedGenres, aVideo.getGenres())
                            && Objects.equals(expectedCastMembers, aVideo.getCastMembers())
                            && Objects.nonNull(aVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), aVideo.getVideo().name())
                            && Objects.nonNull(aVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().name())
                            && Objects.nonNull(aVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), aVideo.getBanner().name())
                            && Objects.nonNull(aVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), aVideo.getThumbnail().name())
                            && Objects.nonNull(aVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), aVideo.getThumbnailHalf().name())
            ));
        }

        @Test
        void Given_a_valid_command_without_cast_members_When_calls_create_video_Then_should_return_a_video_id() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.<CastMemberID>of();

            final Resource expectedVideo = Fixture.Videos.resource(VideoMediaType.VIDEO);
            final Resource expectedTrailer = Fixture.Videos.resource(VideoMediaType.TRAILER);
            final Resource expectedBanner = Fixture.Videos.resource(VideoMediaType.BANNER);
            final Resource expectedThumbnail = Fixture.Videos.resource(VideoMediaType.THUMBNAIL);
            final Resource expectedThumbnailHalf = Fixture.Videos.resource(VideoMediaType.THUMBNAIL_HALF);

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.create(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final CreateVideoOutput actualOutput = createVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).create(argThat(aVideo ->
                    Objects.equals(actualOutput.id(), aVideo.getId().getValue())
                            && Objects.equals(expectedTitle, aVideo.getTitle())
                            && Objects.equals(expectedDescription, aVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, aVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, aVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, aVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, aVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, aVideo.getRating())
                            && Objects.equals(expectedCategories, aVideo.getCategories())
                            && Objects.equals(expectedGenres, aVideo.getGenres())
                            && Objects.equals(expectedCastMembers, aVideo.getCastMembers())
                            && Objects.nonNull(aVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), aVideo.getVideo().name())
                            && Objects.nonNull(aVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().name())
                            && Objects.nonNull(aVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), aVideo.getBanner().name())
                            && Objects.nonNull(aVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), aVideo.getThumbnail().name())
                            && Objects.nonNull(aVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), aVideo.getThumbnailHalf().name())
            ));
        }

        @Test
        void Given_a_valid_command_without_resources_When_calls_create_video_Then_should_return_a_video_id() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(),
                    Fixture.CastMembers.gabriel().getId()
            );

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            when(videoGateway.create(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final CreateVideoOutput actualOutput = createVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).create(argThat(aVideo ->
                    Objects.equals(actualOutput.id(), aVideo.getId().getValue())
                            && Objects.equals(expectedTitle, aVideo.getTitle())
                            && Objects.equals(expectedDescription, aVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, aVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, aVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, aVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, aVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, aVideo.getRating())
                            && Objects.equals(expectedCategories, aVideo.getCategories())
                            && Objects.equals(expectedGenres, aVideo.getGenres())
                            && Objects.equals(expectedCastMembers, aVideo.getCastMembers())
                            && Objects.isNull(aVideo.getVideo())
                            && Objects.isNull(aVideo.getTrailer())
                            && Objects.isNull(aVideo.getBanner())
                            && Objects.isNull(aVideo.getThumbnail())
                            && Objects.isNull(aVideo.getThumbnailHalf())
            ));
        }

        @Test
        void Given_a_valid_command_and_some_non_existing_categories_When_calls_create_Then_should_return_error() {
            // given
            final var anAulasId = Fixture.Categories.aulas().getId();
            final var expectedErrorMessage = "Some categories could not be found: %s"
                    .formatted(anAulasId.getValue());
            final var expectedErrorCount = 1;

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(anAulasId);
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(), Fixture.CastMembers.gabriel().getId());

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, times(1)).existsByIds(any());
            verify(castMemberGateway, times(1)).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_a_valid_command_and_some_non_existing_genres_When_calls_create_Then_should_return_error() {
            // given
            final var aTechId = Fixture.Genres.tech().getId();
            final var expectedErrorMessage = "Some genres could not be found: %s"
                    .formatted(aTechId.getValue());
            final var expectedErrorCount = 1;

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(aTechId);
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(), Fixture.CastMembers.gabriel().getId());

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, times(1)).existsByIds(any());
            verify(castMemberGateway, times(1)).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_a_valid_command_and_some_non_existing_cast_members_When_calls_create_Then_should_return_error() {
            // given
            final var aWesleyId = Fixture.CastMembers.wesley().getId();
            final var expectedErrorMessage = "Some cast members could not be found: %s"
                    .formatted(aWesleyId.getValue());
            final var expectedErrorCount = 1;

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(aWesleyId);

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, times(1)).existsByIds(any());
            verify(castMemberGateway, times(1)).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_a_valid_command_When_calls_create_video_throws_exception_Then_should_call_clear_resources() {
            // given
            final var aWesleyId = Fixture.CastMembers.wesley().getId();
            final var expectedErrorMessage = "An error on create video was observed [videoId: ";

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(aWesleyId);

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when((castMemberGateway.existsByIds(any())))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            when(videoGateway.create(any()))
                    .thenThrow(new RuntimeException("Internal Server Error"));

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(InternalErrorException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertTrue(actualException.getMessage().startsWith(expectedErrorMessage));

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, times(1)).existsByIds(any());
            verify(castMemberGateway, times(1)).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());

            verify(mediaResourceGateway, times(1)).clearResources(any());

            verify(videoGateway, times(1)).create(any());
        }
    }

    @Nested
    @DisplayName("Create a video with invalid command values")
    class CreateWithInvalidCommandValues {

        @Test
        void Given_a_null_title_When_calls_create_video_Then_should_return_domain_exception() {
            // given
            final var expectedErrorMessage = "'title' should not be null";
            final var expectedErrorCount = 1;

            final String aNullTitle = null;
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.<CastMemberID>of();

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            aNullTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).existsByIds(any());
            verify(castMemberGateway, never()).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_an_empty_title_When_calls_create_video_Then_should_return_domain_exception() {
            // given
            final var expectedErrorMessage = "'title' should not be empty";
            final var expectedErrorCount = 1;

            final String anEmptyTitle = " ";
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.<CastMemberID>of();

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            anEmptyTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).existsByIds(any());
            verify(castMemberGateway, never()).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_a_null_rating_When_calls_create_video_Then_should_return_domain_exception() {
            // given
            final var expectedErrorMessage = "'rating' should not be null";
            final var expectedErrorCount = 1;

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final String aNullRating = null;
            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.<CastMemberID>of();

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            aNullRating,
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).existsByIds(any());
            verify(castMemberGateway, never()).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_an_invalid_rating_When_calls_create_video_Then_should_return_domain_exception() {
            // given
            final var expectedErrorMessage = "'rating' should not be null";
            final var expectedErrorCount = 1;

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var anInvalidRating = "invalid-rating";
            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.<CastMemberID>of();

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            expectedLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            anInvalidRating,
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).existsByIds(any());
            verify(castMemberGateway, never()).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }

        @Test
        void Given_a_null_launched_at_When_calls_create_video_Then_should_return_domain_exception() {
            // given
            final var expectedErrorMessage = "'launchedAt' should not be null";
            final var expectedErrorCount = 1;

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final Integer aNullLaunchedAt = null;
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.<CastMemberID>of();

            final Resource expectedVideo = null;
            final Resource expectedTrailer = null;
            final Resource expectedBanner = null;
            final Resource expectedThumbnail = null;
            final Resource expectedThumbnailHalf = null;

            final var aCommand =
                    CreateVideoCommand.with(
                            expectedTitle,
                            expectedDescription,
                            aNullLaunchedAt,
                            expectedDuration,
                            expectedReleaseStatus,
                            expectedPublishingStatus,
                            expectedRating.getName(),
                            asString(expectedCategories),
                            asString(expectedGenres),
                            asString(expectedCastMembers),
                            expectedVideo,
                            expectedTrailer,
                            expectedBanner,
                            expectedThumbnail,
                            expectedThumbnailHalf
                    );

            // when
            Executable invalidMethodCall = () -> createVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(DomainException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(categoryGateway, never()).existsByIds(any());
            verify(genreGateway, never()).existsByIds(any());
            verify(castMemberGateway, never()).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());
            verify(videoGateway, never()).create(any());
        }
    }

    private void mockImageMedia() {
        when(mediaResourceGateway.storeImage(any(), any()))
                .thenAnswer(it -> {
                    final var resource = it.getArgument(1, VideoResource.class);
                    return Fixture.Videos.image(resource.type());
                });
    }

    private void mockAudioVideoMedia() {
        when(mediaResourceGateway.storeAudioVideo(any(), any()))
                .thenAnswer(it -> {
                    final var resource = it.getArgument(1, VideoResource.class);
                    return Fixture.Videos.audioVideo(resource.type());
                });
    }
}
