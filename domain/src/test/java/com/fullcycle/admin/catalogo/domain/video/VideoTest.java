package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.utils.InstantUtils;
import org.junit.jupiter.api.*;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VideoTest extends UnitTest {

    @DisplayName("Create a video with valid params")
    @Nested
    class CreateWithValidParams {

        @Test
        void Given_valid_params_When_calls_newVideo_Then_should_instantiate_a_video() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            // when
            final var actualVideo = Video.newVideo(aBuilder);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertNull(actualVideo.getVideo());
            assertNull(actualVideo.getTrailer());
            assertNull(actualVideo.getBanner());
            assertNull(actualVideo.getThumbnail());
            assertNull(actualVideo.getThumbnailHalf());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(actualVideo.getCreatedAt(), actualVideo.getUpdatedAt());
            assertTrue(actualVideo.getDomainEvents().isEmpty());
        }

        @Test
        void Given_valid_params_When_calls_with_Then_should_create_without_events() {
            // given
            final var aTitle = "A title";
            final var aDescription = "A description";
            final var aLaunchedAt = Year.of(1900);
            final var aDuration = 0.0;
            final var aReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var aPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var aRating = Rating.AGE_10;
            final var aCategories = Set.<CategoryID>of();
            final var aGenres = Set.<GenreID>of();
            final var aCastMembers = Set.<CastMemberID>of();

            final var aBuilder = new Video.Builder(aTitle,
                    aDescription,
                    aLaunchedAt,
                    aRating)
                    .id(VideoID.unique())
                    .duration(aDuration)
                    .releaseStatus(aReleaseStatus)
                    .publishingStatus(aPublishingStatus)
                    .categories(aCategories)
                    .genres(aGenres)
                    .castMembers(aCastMembers)
                    .createdAt(InstantUtils.now())
                    .updatedAt(InstantUtils.now());


            // when
            final var actualVideo = Video.with(aBuilder);

            // then
            assertNotNull(actualVideo.getDomainEvents());
        }
    }

    @DisplayName("Update a video with valid params")
    @Nested
    class UpdateWithValidParams {

        @Test
        void Given_valid_params_When_calls_update_Then_should_return_video_updated() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());
            final var expectedEvent = new VideoMediaCreated("ID", "file");
            final var expecteEventCount = 1;

            final var aTitle = "A title";
            final var aDescription = "A description";
            final var aLaunchedAt = Year.of(1900);
            final var aDuration = 0.0;
            final var aReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var aPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var aRating = Rating.AGE_10;
            final var aCategories = Set.<CategoryID>of();
            final var aGenres = Set.<GenreID>of();
            final var aCastMembers = Set.<CastMemberID>of();

            final var aCreateBuilder = new Video.Builder(aTitle,
                    aDescription,
                    aLaunchedAt,
                    aRating)
                    .duration(aDuration)
                    .releaseStatus(aReleaseStatus)
                    .publishingStatus(aPublishingStatus)
                    .categories(aCategories)
                    .genres(aGenres)
                    .castMembers(aCastMembers);

            final var aVideo = Video.newVideo(aCreateBuilder);

            final var anUpdateBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            aVideo.registerEvent(expectedEvent);

            // when
            final var actualVideo = Video.with(aVideo).update(anUpdateBuilder);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertNull(actualVideo.getVideo());
            assertNull(actualVideo.getTrailer());
            assertNull(actualVideo.getBanner());
            assertNull(actualVideo.getThumbnail());
            assertNull(actualVideo.getThumbnailHalf());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));

            assertEquals(expecteEventCount, actualVideo.getDomainEvents().size());
            assertEquals(expectedEvent, actualVideo.getDomainEvents().get(0));
        }

        @Test
        void Given_valid_params_When_calls_configureVideo_Then_should_return_video_updated() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            final var aVideo = Video.newVideo(aBuilder);

            final var aVideoMediaId = "123";
            final var aChecksum = "abc";
            final var aName = "Video.mp4";
            final var aRawLocation = "/123/videos";
            final var anEncodedLocation = "/123/videos";
            final var aMediaStatus = MediaStatus.PENDING;
            final var aVideoMedia = AudioVideoMedia
                    .with(aVideoMediaId, aChecksum, aName, aRawLocation, anEncodedLocation, aMediaStatus);

            final var expectedDomainEventsSize = 1;

            // when
            final var actualVideo = Video.with(aVideo).configureVideo(aVideoMedia);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertEquals(aVideoMedia, actualVideo.getVideo());

            assertNull(actualVideo.getTrailer());
            assertNull(actualVideo.getBanner());
            assertNull(actualVideo.getThumbnail());
            assertNull(actualVideo.getThumbnailHalf());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));

            assertEquals(expectedDomainEventsSize, actualVideo.getDomainEvents().size());
            final var actualEvent = (VideoMediaCreated) actualVideo.getDomainEvents().get(0);
            assertEquals(aVideoMedia.id(), actualEvent.resourceId());
            assertEquals(aVideoMedia.rawLocation(), actualEvent.filePath());
            assertNotNull(actualEvent.occurredOn());
        }

        @Test
        void Given_valid_params_When_calls_configureTrailer_Then_should_return_video_updated() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            final var aVideo = Video.newVideo(aBuilder);

            final var anImageId = "456";
            final var aChecksum = "def";
            final var aName = "Trailer.mp4";
            final var aRawLocation = "/123/trailers";
            final var anEncodedLocation = "/123/trailers";
            final var aMediaStatus = MediaStatus.PENDING;
            final var aTrailerMedia = AudioVideoMedia
                    .with(anImageId, aChecksum, aName, aRawLocation, anEncodedLocation, aMediaStatus);

            final var expectedDomainEventsSize = 1;

            // when
            final var actualVideo = Video.with(aVideo).configureTrailer(aTrailerMedia);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertEquals(aTrailerMedia, actualVideo.getTrailer());

            assertNull(actualVideo.getVideo());
            assertNull(actualVideo.getBanner());
            assertNull(actualVideo.getThumbnail());
            assertNull(actualVideo.getThumbnailHalf());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));

            assertEquals(expectedDomainEventsSize, actualVideo.getDomainEvents().size());
            final var actualEvent = (VideoMediaCreated) actualVideo.getDomainEvents().get(0);
            assertEquals(aTrailerMedia.id(), actualEvent.resourceId());
            assertEquals(aTrailerMedia.rawLocation(), actualEvent.filePath());
            assertNotNull(actualEvent.occurredOn());
        }

        @Test
        void Given_valid_params_When_calls_configureBanner_Then_should_return_video_updated() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            final var aVideo = Video.newVideo(aBuilder);

            final var aChecksum = "ghi";
            final var aName = "Banner.png";
            final var aRawLocation = "/123/banners";
            final var aBannerMedia = ImageMedia.with(aChecksum, aName, aRawLocation);

            // when
            final var actualVideo = Video.with(aVideo).configureBanner(aBannerMedia);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertEquals(aBannerMedia, actualVideo.getBanner());

            assertNull(actualVideo.getVideo());
            assertNull(actualVideo.getTrailer());
            assertNull(actualVideo.getThumbnail());
            assertNull(actualVideo.getThumbnailHalf());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        }

        @Test
        void Given_valid_params_When_calls_configureThumbnail_Then_should_return_video_updated() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            final var aVideo = Video.newVideo(aBuilder);

            final var aChecksum = "jkl";
            final var aName = "Thumbnail.png";
            final var aRawLocation = "/123/thumbnails";
            final var aThumbnailMedia = ImageMedia.with(aChecksum, aName, aRawLocation);

            // when
            final var actualVideo = Video.with(aVideo).configureThumbnail(aThumbnailMedia);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertEquals(aThumbnailMedia, actualVideo.getThumbnail());

            assertNull(actualVideo.getVideo());
            assertNull(actualVideo.getTrailer());
            assertNull(actualVideo.getBanner());
            assertNull(actualVideo.getThumbnailHalf());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        }

        @Test
        void Given_valid_params_When_calls_configureThumbnailHalf_Then_should_return_video_updated() {
            // given
            final var expectedTitle = "System Design Interviews";
            final var expectedDescription = """
                    Disclaimer: o estudo de caso apresentado tem fins educacionais e representa nossas opiniões pessoais.
                    Esse vídeo faz parte da Imersão Full Stack && Full Cycle.
                    Para acessar todas as aulas, lives e desafios, acesse:
                    https://imersao.fullcycle.com.br/
                    """;
            final var expectedLaunchedAt = Year.of(2023);
            final var expectedDuration = 120.1;
            final var expectedReleaseStatus = ReleaseStatus.NOT_RELEASED;
            final var expectedPublishingStatus = PublishingStatus.NOT_PUBLISHED;
            final var expectedRating = Rating.L;
            final var expectedCategories = Set.of(CategoryID.unique());
            final var expectedGenres = Set.of(GenreID.unique());
            final var expectedCastMembers = Set.of(CastMemberID.unique());

            final var aBuilder = new Video.Builder(expectedTitle,
                    expectedDescription,
                    expectedLaunchedAt,
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            final var aVideo = Video.newVideo(aBuilder);

            final var aChecksum = "jkl";
            final var aName = "ThumbnailHalf.png";
            final var aRawLocation = "/123/thumbnailhalves";
            final var aThumbnailHalfMedia = ImageMedia.with(aChecksum, aName, aRawLocation);

            // when
            final var actualVideo = Video.with(aVideo).configureThumbnailHalf(aThumbnailHalfMedia);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());

            assertEquals(aThumbnailHalfMedia, actualVideo.getThumbnailHalf());

            assertNull(actualVideo.getVideo());
            assertNull(actualVideo.getTrailer());
            assertNull(actualVideo.getBanner());
            assertNull(actualVideo.getThumbnail());

            assertNotNull(actualVideo.getCreatedAt());
            assertNotNull(actualVideo.getUpdatedAt());
            assertEquals(aVideo.getCreatedAt(), actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
        }
    }
}
