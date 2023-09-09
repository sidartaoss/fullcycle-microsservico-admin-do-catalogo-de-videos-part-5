package com.fullcycle.admin.catalogo.application.video.retrieve.get;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;
import com.fullcycle.admin.catalogo.domain.video.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class GetVideoByIdUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultGetVideoByIdUseCase getVideoByIdUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Nested
    @DisplayName("Get a video by id with a valid identifier")
    class GetGenreByIdWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_get_video_by_id_Then_should_return_an_ouput_video() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Year.of(Fixture.year());
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();
            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
            final var expectedCastMembers = Set.of(
                    Fixture.CastMembers.wesley().getId(),
                    Fixture.CastMembers.gabriel().getId());

            final var expectedVideo = audioVideo(VideoMediaType.VIDEO);
            final var expectedTrailer = audioVideo(VideoMediaType.TRAILER);
            final var expectedBanner = imageMedia(VideoMediaType.BANNER);
            final var expectedThumbnail = imageMedia(VideoMediaType.THUMBNAIL);
            final var expectedThumbnailHalf = imageMedia(VideoMediaType.THUMBNAIL_HALF);

            final var aBuilder = new Video.Builder(
                    expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers)
                    .video(expectedVideo)
                    .trailer(expectedTrailer)
                    .banner(expectedBanner)
                    .thumbnail(expectedThumbnail)
                    .thumbnailHalf(expectedThumbnailHalf);

            final var aVideo = Video.newVideo(aBuilder);
            final var expectedId = aVideo.getId();

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.of(Video.with(aVideo)));

            // when
            final var actualOutput = getVideoByIdUseCase.execute(expectedId.getValue());

            // then
            assertEquals(expectedId.getValue(), actualOutput.id());
            assertEquals(expectedTitle, actualOutput.title());
            assertEquals(expectedDescription, actualOutput.description());
            assertEquals(expectedLaunchedAt.getValue(), actualOutput.launchedAt());
            assertEquals(expectedDuration, actualOutput.duration());
            assertEquals(expectedReleaseStatus, actualOutput.releaseStatus());
            assertEquals(expectedPublishingStatus, actualOutput.publishingStatus());
            assertEquals(expectedRating.getName(), actualOutput.rating());
            assertEquals(mapTo(expectedCategories, Identifier::getValue), actualOutput.categories());
            assertEquals(mapTo(expectedGenres, Identifier::getValue), actualOutput.genres());
            assertEquals(mapTo(expectedCastMembers, Identifier::getValue), actualOutput.castMembers());
            assertEquals(aVideo.getCreatedAt(), actualOutput.createdAt());
            assertEquals(aVideo.getUpdatedAt(), actualOutput.updatedAt());
            assertEquals(expectedVideo, actualOutput.video());
            assertEquals(expectedTrailer, actualOutput.trailer());
            assertEquals(expectedBanner, actualOutput.banner());
            assertEquals(expectedThumbnail, actualOutput.thumbnail());
            assertEquals(expectedThumbnailHalf, actualOutput.thumbnailHalf());
        }
    }

    @Nested
    @DisplayName("Get a video by id with an invalid identifier")
    class GetGenreByIdWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_get_video_by_id_Then_should_return_not_found_error() {
            // given
            final var expectedId = VideoID.from("123");
            final var expectedErrorMessage = "Video with ID %s was not found"
                    .formatted(expectedId.getValue());

            when(videoGateway.findById(any()))
                    .thenReturn(Optional.empty());

            // when
            Executable invalidMethodCall = () -> getVideoByIdUseCase.execute(expectedId.getValue());

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());
        }
    }

    private AudioVideoMedia audioVideo(final VideoMediaType aType) {
        final var checksum = IdUtils.uuid();
        return AudioVideoMedia.with(
                IdUtils.uuid(),
                checksum,
                aType.name().toLowerCase(),
                "/videos/" + checksum,
                "/videos-encoded/" + checksum,
                MediaStatus.PENDING);
    }

    private ImageMedia imageMedia(final VideoMediaType aType) {
        final var checksum = IdUtils.uuid();
        return ImageMedia.with(
                checksum,
                aType.name().toLowerCase(),
                "/images/" + checksum);
    }
}
