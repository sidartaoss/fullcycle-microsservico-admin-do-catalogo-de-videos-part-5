package com.fullcycle.admin.catalogo.application.video.update;

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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UpdateVideoUseCaseTest extends UseCaseTest {

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
    private DefaultUpdateVideoUseCase updateVideoUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway, categoryGateway, genreGateway, castMemberGateway, mediaResourceGateway);
    }

    @Nested
    @DisplayName("Update a video with valid command values")
    class UpdateWithValidCommandValues {

        @Test
        void Given_a_valid_command_When_calls_update_video_Then_should_return_a_video_id() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(actualOutput.id(), actualVideo.getId().getValue())
                            && Objects.equals(expectedTitle, actualVideo.getTitle())
                            && Objects.equals(expectedDescription, actualVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, actualVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, actualVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, actualVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, actualVideo.getRating())
                            && Objects.equals(expectedCategories, actualVideo.getCategories())
                            && Objects.equals(expectedGenres, actualVideo.getGenres())
                            && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                            && Objects.nonNull(actualVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), actualVideo.getVideo().name())
                            && Objects.nonNull(actualVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().name())
                            && Objects.nonNull(actualVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), actualVideo.getBanner().name())
                            && Objects.nonNull(actualVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().name())
                            && Objects.nonNull(actualVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name())
                            && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                            && actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_without_categories_When_calls_update_video_Then_should_return_a_video_id() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).findById(expectedId);

            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(actualOutput.id(), actualVideo.getId().getValue())
                            && Objects.equals(expectedTitle, actualVideo.getTitle())
                            && Objects.equals(expectedDescription, actualVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, actualVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, actualVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, actualVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, actualVideo.getRating())
                            && Objects.equals(expectedCategories, actualVideo.getCategories())
                            && Objects.equals(expectedGenres, actualVideo.getGenres())
                            && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                            && Objects.nonNull(actualVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), actualVideo.getVideo().name())
                            && Objects.nonNull(actualVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().name())
                            && Objects.nonNull(actualVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), actualVideo.getBanner().name())
                            && Objects.nonNull(actualVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().name())
                            && Objects.nonNull(actualVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name())
                            && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                            && actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_without_genres_When_calls_update_video_Then_should_return_a_video_id() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).findById(expectedId);

            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(actualOutput.id(), actualVideo.getId().getValue())
                            && Objects.equals(expectedTitle, actualVideo.getTitle())
                            && Objects.equals(expectedDescription, actualVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, actualVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, actualVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, actualVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, actualVideo.getRating())
                            && Objects.equals(expectedCategories, actualVideo.getCategories())
                            && Objects.equals(expectedGenres, actualVideo.getGenres())
                            && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                            && Objects.nonNull(actualVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), actualVideo.getVideo().name())
                            && Objects.nonNull(actualVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().name())
                            && Objects.nonNull(actualVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), actualVideo.getBanner().name())
                            && Objects.nonNull(actualVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().name())
                            && Objects.nonNull(actualVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name())
                            && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                            && actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_without_cast_members_When_calls_update_video_Then_should_return_a_video_id() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            mockImageMedia();
            mockAudioVideoMedia();

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).findById(expectedId);

            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(actualOutput.id(), actualVideo.getId().getValue())
                            && Objects.equals(expectedTitle, actualVideo.getTitle())
                            && Objects.equals(expectedDescription, actualVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, actualVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, actualVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, actualVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, actualVideo.getRating())
                            && Objects.equals(expectedCategories, actualVideo.getCategories())
                            && Objects.equals(expectedGenres, actualVideo.getGenres())
                            && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                            && Objects.nonNull(actualVideo.getVideo())
                            && Objects.equals(expectedVideo.name(), actualVideo.getVideo().name())
                            && Objects.nonNull(actualVideo.getTrailer())
                            && Objects.equals(expectedTrailer.name(), actualVideo.getTrailer().name())
                            && Objects.nonNull(actualVideo.getBanner())
                            && Objects.equals(expectedBanner.name(), actualVideo.getBanner().name())
                            && Objects.nonNull(actualVideo.getThumbnail())
                            && Objects.equals(expectedThumbnail.name(), actualVideo.getThumbnail().name())
                            && Objects.nonNull(actualVideo.getThumbnailHalf())
                            && Objects.equals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name())
                            && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                            && actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_without_resources_When_calls_update_video_Then_should_return_a_video_id() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            when(videoGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateVideoUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(videoGateway, times(1)).findById(expectedId);

            verify(videoGateway, times(1)).update(argThat(actualVideo ->
                    Objects.equals(actualOutput.id(), actualVideo.getId().getValue())
                            && Objects.equals(expectedTitle, actualVideo.getTitle())
                            && Objects.equals(expectedDescription, actualVideo.getDescription())
                            && Objects.equals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue())
                            && Objects.equals(expectedDuration, actualVideo.getDuration())
                            && Objects.equals(expectedReleaseStatus, actualVideo.getReleaseStatus())
                            && Objects.equals(expectedPublishingStatus, actualVideo.getPublishingStatus())
                            && Objects.equals(expectedRating, actualVideo.getRating())
                            && Objects.equals(expectedCategories, actualVideo.getCategories())
                            && Objects.equals(expectedGenres, actualVideo.getGenres())
                            && Objects.equals(expectedCastMembers, actualVideo.getCastMembers())
                            && Objects.isNull(actualVideo.getVideo())
                            && Objects.isNull(actualVideo.getTrailer())
                            && Objects.isNull(actualVideo.getBanner())
                            && Objects.isNull(actualVideo.getThumbnail())
                            && Objects.isNull(actualVideo.getThumbnailHalf())
                            && Objects.equals(aVideo.getCreatedAt(), actualVideo.getCreatedAt())
                            && actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt())
            ));
        }

        @Test
        void Given_a_valid_command_and_some_non_existing_categories_When_calls_update_Then_should_return_error() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
        void Given_a_valid_command_and_some_non_existing_genres_When_calls_update_Then_should_return_error() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(castMemberGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
        void Given_a_valid_command_and_some_non_existing_cast_members_When_calls_update_Then_should_return_error() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
        void Given_a_valid_command_When_calls_update_video_throws_exception_Then_should_not_call_clear_resources() {
            // given
            final var aWesleyId = Fixture.CastMembers.wesley().getId();
            final var expectedErrorMessage = "An error on update video was observed [videoId: ";

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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            when(categoryGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedCategories));

            when(genreGateway.existsByIds(any()))
                    .thenReturn(new ArrayList<>(expectedGenres));

            when((castMemberGateway.existsByIds(any())))
                    .thenReturn(new ArrayList<>(expectedCastMembers));

            when(videoGateway.update(any()))
                    .thenThrow(new RuntimeException("Internal Server Error"));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(InternalErrorException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertTrue(actualException.getMessage().startsWith(expectedErrorMessage));

            verify(categoryGateway, times(1)).existsByIds(any());
            verify(genreGateway, times(1)).existsByIds(any());
            verify(castMemberGateway, times(1)).existsByIds(any());
            verify(mediaResourceGateway, never()).storeImage(any(), any());
            verify(mediaResourceGateway, never()).storeAudioVideo(any(), any());

            verify(mediaResourceGateway, never()).clearResources(any());

            verify(videoGateway, times(1)).update(any());
        }
    }

    @Nested
    @DisplayName("Update a video with invalid command values")
    class UpdateWithInvalidCommandValues {

        @Test
        void Given_a_null_title_When_calls_update_video_Then_should_return_domain_exception() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
            verify(videoGateway, never()).update(any());
        }

        @Test
        void Given_an_empty_title_When_calls_update_video_Then_should_return_domain_exception() {
            // given
            final var expectedErrorMessage = "'title' should not be empty";
            final var expectedErrorCount = 1;

            final var anEmptyTitle = " ";
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
            verify(videoGateway, never()).update(any());
        }

        @Test
        void Given_a_null_rating_When_calls_update_video_Then_should_return_domain_exception() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
            verify(videoGateway, never()).update(any());
        }

        @Test
        void Given_an_invalid_rating_When_calls_update_video_Then_should_return_domain_exception() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
            verify(videoGateway, never()).update(any());
        }

        @Test
        void Given_a_null_launched_at_When_calls_update_video_Then_should_return_domain_exception() {
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

            final var aVideo = Fixture.Videos.systemDesign();
            final var expectedId = aVideo.getId();

            final var aCommand =
                    UpdateVideoCommand.with(
                            expectedId.getValue(),
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

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            // when
            Executable invalidMethodCall = () -> updateVideoUseCase.execute(aCommand);

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
            verify(videoGateway, never()).update(any());
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
