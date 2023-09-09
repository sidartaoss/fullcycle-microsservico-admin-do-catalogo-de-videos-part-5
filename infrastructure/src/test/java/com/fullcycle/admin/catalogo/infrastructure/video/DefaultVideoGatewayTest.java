package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.Genre;
import com.fullcycle.admin.catalogo.domain.genre.GenreGateway;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
public class DefaultVideoGatewayTest {

    @Autowired
    private DefaultVideoGateway defaultVideoGateway;

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CategoryGateway categoryGateway;

    @Autowired
    private GenreGateway genreGateway;

    @Autowired
    private VideoRepository videoRepository;

    private CastMember wesley;
    private CastMember gabriel;
    private Category aulas;
    private Category lives;
    private Genre tech;
    private Genre business;

    @BeforeEach
    void setUp() {
        wesley = castMemberGateway.create(Fixture.CastMembers.wesley());
        gabriel = castMemberGateway.create(Fixture.CastMembers.gabriel());

        aulas = categoryGateway.create(Fixture.Categories.aulas());
        lives = categoryGateway.create(Fixture.Categories.lives());

        tech = genreGateway.create(Fixture.Genres.tech());
        business = genreGateway.create(Fixture.Genres.business());
    }

    @Nested
    @DisplayName("Create with valid video values")
    class CreateWithValidVideoValues {

        @Test
        @Transactional
        void Given_a_valid_video_When_calls_create_video_Then_should_persist_it() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();

            final var expectedCategories = Set.of(aulas.getId());
            final var expectedGenres = Set.of(tech.getId());
            final var expectedCastMembers = Set.of(wesley.getId());

            final var expectedVideo =
                    AudioVideoMedia.with("123", "video", "/media/video");
            final var expectedTrailer =
                    AudioVideoMedia.with("456", "trailer", "/media/trailer");

            final var expectedBanner =
                    ImageMedia.with("123", "banner", "/media/banner");
            final var expectedThumbnail =
                    ImageMedia.with("456", "thumbnail", "/media/thumbnail");
            final var expectedThumbnailHalf =
                    ImageMedia.with("789", "thumbnailHalf", "/media/thumbnailHalf");

            final var aBuilder = new Video.Builder(
                    expectedTitle,
                    expectedDescription,
                    Year.of(expectedLaunchedAt),
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

            // when
            final var actualVideo = defaultVideoGateway.create(aVideo);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());

            assertEquals(aVideo.getId().getValue(), actualVideo.getId().getValue());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());
            assertEquals(expectedVideo.name(), actualVideo.getVideo().name());
            assertEquals(expectedTrailer.name(), actualVideo.getTrailer().name());
            assertEquals(expectedBanner.name(), actualVideo.getBanner().name());
            assertEquals(expectedThumbnail.name(), actualVideo.getThumbnail().name());
            assertEquals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name());

            videoRepository.findById(actualVideo.getId().getValue())
                    .ifPresent(persistedVideo -> {
                        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
                        assertEquals(expectedTitle, persistedVideo.getTitle());
                        assertEquals(expectedDescription, persistedVideo.getDescription());
                        assertEquals(expectedLaunchedAt, persistedVideo.getYearLaunched());
                        assertEquals(expectedDuration, persistedVideo.getDuration());
                        assertEquals(expectedReleaseStatus, persistedVideo.getReleaseStatus());
                        assertEquals(expectedPublishingStatus, persistedVideo.getPublishingStatus());
                        assertEquals(expectedRating, persistedVideo.getRating());
                        assertEquals(expectedCategories, persistedVideo.getCategoriesID());
                        assertEquals(expectedGenres, persistedVideo.getGenresID());
                        assertEquals(expectedCastMembers, persistedVideo.getCastMembersID());
                        assertEquals(expectedVideo.name(), persistedVideo.getVideo().getName());
                        assertEquals(expectedTrailer.name(), persistedVideo.getTrailer().getName());
                        assertEquals(expectedBanner.name(), persistedVideo.getBanner().getName());
                        assertEquals(expectedThumbnail.name(), persistedVideo.getThumbnail().getName());
                        assertEquals(expectedThumbnailHalf.name(), persistedVideo.getThumbnailHalf().getName());
                    });
        }

        @Test
        @Transactional
        void Given_a_valid_video_without_relations_When_calls_create_video_Then_should_persist_it() {
            // given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();

            final var expectedCategories = Set.<CategoryID>of();
            final var expectedGenres = Set.<GenreID>of();
            final var expectedCastMembers = Set.<CastMemberID>of();

            final var aBuilder = new Video.Builder(
                    expectedTitle,
                    expectedDescription,
                    Year.of(expectedLaunchedAt),
                    expectedRating)
                    .duration(expectedDuration)
                    .releaseStatus(expectedReleaseStatus)
                    .publishingStatus(expectedPublishingStatus)
                    .categories(expectedCategories)
                    .genres(expectedGenres)
                    .castMembers(expectedCastMembers);

            final var aVideo = Video.newVideo(aBuilder);

            // when
            final var actualVideo = defaultVideoGateway.create(aVideo);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());

            assertEquals(aVideo.getId().getValue(), actualVideo.getId().getValue());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue());
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

            videoRepository.findById(actualVideo.getId().getValue())
                    .ifPresent(persistedVideo -> {
                        assertEquals(aVideo.getId().getValue(), persistedVideo.getId());
                        assertEquals(expectedTitle, persistedVideo.getTitle());
                        assertEquals(expectedDescription, persistedVideo.getDescription());
                        assertEquals(expectedLaunchedAt, persistedVideo.getYearLaunched());
                        assertEquals(expectedDuration, persistedVideo.getDuration());
                        assertEquals(expectedReleaseStatus, persistedVideo.getReleaseStatus());
                        assertEquals(expectedPublishingStatus, persistedVideo.getPublishingStatus());
                        assertEquals(expectedRating, persistedVideo.getRating());
                        assertEquals(expectedCategories, persistedVideo.getCategoriesID());
                        assertEquals(expectedGenres, persistedVideo.getGenresID());
                        assertEquals(expectedCastMembers, persistedVideo.getCastMembersID());
                        assertNull(persistedVideo.getVideo());
                        assertNull(persistedVideo.getTrailer());
                        assertNull(persistedVideo.getBanner());
                        assertNull(persistedVideo.getThumbnail());
                        assertNull(persistedVideo.getThumbnailHalf());
                    });
        }
    }

    @Nested
    @DisplayName("Update with valid video values")
    class UpdateWithValidVideoValues {

        @Test
        @Transactional
        void Given_a_valid_video_When_calls_update_video_Then_should_update_it() {
            // given
            final var aBuilderCreate = new Video.Builder(
                    Fixture.title(),
                    Fixture.Videos.description(),
                    Year.of(Fixture.year()),
                    Fixture.Videos.rating())
                    .duration(Fixture.duration())
                    .releaseStatus(Fixture.Videos.releaseStatus())
                    .publishingStatus(Fixture.Videos.publishingStatus())
                    .categories(Set.<CategoryID>of())
                    .genres(Set.<GenreID>of())
                    .castMembers(Set.<CastMemberID>of());

            final var aVideo = defaultVideoGateway.create(Video.newVideo(aBuilderCreate));

            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();

            final var expectedCategories = Set.of(aulas.getId());
            final var expectedGenres = Set.of(tech.getId());
            final var expectedCastMembers = Set.of(wesley.getId());

            final var expectedVideo =
                    AudioVideoMedia.with("123", "video", "/media/video");
            final var expectedTrailer =
                    AudioVideoMedia.with("456", "trailer", "/media/trailer");

            final var expectedBanner =
                    ImageMedia.with("123", "banner", "/media/banner");
            final var expectedThumbnail =
                    ImageMedia.with("456", "thumbnail", "/media/thumbnail");
            final var expectedThumbnailHalf =
                    ImageMedia.with("789", "thumbnailHalf", "/media/thumbnailHalf");

            final var aBuilderUpdate = new Video.Builder(
                    expectedTitle,
                    expectedDescription,
                    Year.of(expectedLaunchedAt),
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

            final var domainUpdatedVideo = Video.with(aVideo)
                    .update(aBuilderUpdate);

            // when
            final var actualVideo = defaultVideoGateway.update(domainUpdatedVideo);

            // then
            assertNotNull(actualVideo);
            assertNotNull(actualVideo.getId());

            assertEquals(aVideo.getId().getValue(), actualVideo.getId().getValue());
            assertEquals(expectedTitle, actualVideo.getTitle());
            assertEquals(expectedDescription, actualVideo.getDescription());
            assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue());
            assertEquals(expectedDuration, actualVideo.getDuration());
            assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
            assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
            assertEquals(expectedRating, actualVideo.getRating());
            assertEquals(expectedCategories, actualVideo.getCategories());
            assertEquals(expectedGenres, actualVideo.getGenres());
            assertEquals(expectedCastMembers, actualVideo.getCastMembers());
            assertEquals(expectedVideo.name(), actualVideo.getVideo().name());
            assertEquals(expectedTrailer.name(), actualVideo.getTrailer().name());
            assertEquals(expectedBanner.name(), actualVideo.getBanner().name());
            assertEquals(expectedThumbnail.name(), actualVideo.getThumbnail().name());
            assertEquals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name());
            assertNotNull(actualVideo.getCreatedAt());
            assertTrue(actualVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));

            videoRepository.findById(actualVideo.getId().getValue())
                    .ifPresent(dbUpdatedVideo -> {
                        assertEquals(aVideo.getId().getValue(), dbUpdatedVideo.getId());
                        assertEquals(expectedTitle, dbUpdatedVideo.getTitle());
                        assertEquals(expectedDescription, dbUpdatedVideo.getDescription());
                        assertEquals(expectedLaunchedAt, dbUpdatedVideo.getYearLaunched());
                        assertEquals(expectedDuration, dbUpdatedVideo.getDuration());
                        assertEquals(expectedReleaseStatus, dbUpdatedVideo.getReleaseStatus());
                        assertEquals(expectedPublishingStatus, dbUpdatedVideo.getPublishingStatus());
                        assertEquals(expectedRating, dbUpdatedVideo.getRating());
                        assertEquals(expectedCategories, dbUpdatedVideo.getCategoriesID());
                        assertEquals(expectedGenres, dbUpdatedVideo.getGenresID());
                        assertEquals(expectedCastMembers, dbUpdatedVideo.getCastMembersID());
                        assertEquals(expectedVideo.name(), dbUpdatedVideo.getVideo().getName());
                        assertEquals(expectedTrailer.name(), dbUpdatedVideo.getTrailer().getName());
                        assertEquals(expectedBanner.name(), dbUpdatedVideo.getBanner().getName());
                        assertEquals(expectedThumbnail.name(), dbUpdatedVideo.getThumbnail().getName());
                        assertEquals(expectedThumbnailHalf.name(), dbUpdatedVideo.getThumbnailHalf().getName());
                        assertNotNull(dbUpdatedVideo.getCreatedAt());
                        assertTrue(dbUpdatedVideo.getUpdatedAt().isAfter(aVideo.getUpdatedAt()));
                    });
        }
    }

    @Nested
    @DisplayName("Delete with valid video values")
    class DeleteWithValidVideoValues {

        @Test
        void Given_a_valid_video_id_When_calls_delete_by_id_Then_should_delete_it() {
            // given
            final var aBuilderCreate = new Video.Builder(
                    Fixture.title(),
                    Fixture.Videos.description(),
                    Year.of(Fixture.year()),
                    Fixture.Videos.rating())
                    .duration(Fixture.duration())
                    .releaseStatus(Fixture.Videos.releaseStatus())
                    .publishingStatus(Fixture.Videos.publishingStatus())
                    .categories(Set.<CategoryID>of())
                    .genres(Set.<GenreID>of())
                    .castMembers(Set.<CastMemberID>of());

            final var aVideo = defaultVideoGateway.create(Video.newVideo(aBuilderCreate));
            final var anId = aVideo.getId();

            assertEquals(1, videoRepository.count());

            // when
            defaultVideoGateway.deleteById(anId);

            // then
            assertEquals(0, videoRepository.count());
        }

        @Test
        void Given_an_invalid_video_id_When_calls_delete_by_id_Then_should_be_ok() {
            // given
            final var aBuilderCreate = new Video.Builder(
                    Fixture.title(),
                    Fixture.Videos.description(),
                    Year.of(Fixture.year()),
                    Fixture.Videos.rating())
                    .duration(Fixture.duration())
                    .releaseStatus(Fixture.Videos.releaseStatus())
                    .publishingStatus(Fixture.Videos.publishingStatus())
                    .categories(Set.<CategoryID>of())
                    .genres(Set.<GenreID>of())
                    .castMembers(Set.<CastMemberID>of());

            defaultVideoGateway.create(Video.newVideo(aBuilderCreate));
            final var anId = VideoID.unique();

            assertEquals(1, videoRepository.count());

            // when
            defaultVideoGateway.deleteById(anId);

            // then
            assertEquals(1, videoRepository.count());
        }
    }

    @Nested
    @DisplayName("Get video by a valid identifier")
    class GetVideoByValidIdentifier {

        @Test
        void Given_a_valid_video_When_calls_get_video_by_id_Then_should_return_it() {
            // Given
            final var expectedTitle = Fixture.title();
            final var expectedDescription = Fixture.Videos.description();
            final var expectedLaunchedAt = Fixture.year();
            final var expectedDuration = Fixture.duration();
            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
            final var expectedRating = Fixture.Videos.rating();

            final var expectedCategories = Set.of(aulas.getId());
            final var expectedGenres = Set.of(tech.getId());
            final var expectedCastMembers = Set.of(wesley.getId());

            final var expectedVideo =
                    AudioVideoMedia.with("123", "video", "/media/video");
            final var expectedTrailer =
                    AudioVideoMedia.with("456", "trailer", "/media/trailer");

            final var expectedBanner =
                    ImageMedia.with("123", "banner", "/media/banner");
            final var expectedThumbnail =
                    ImageMedia.with("456", "thumbnail", "/media/thumbnail");
            final var expectedThumbnailHalf =
                    ImageMedia.with("789", "thumbnailHalf", "/media/thumbnailHalf");

            final var aBuilder = new Video.Builder(
                    expectedTitle,
                    expectedDescription,
                    Year.of(expectedLaunchedAt),
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

            final var aVideo = defaultVideoGateway.create(Video.newVideo(aBuilder));

            // When & Then
            defaultVideoGateway.findById(aVideo.getId())
                    .ifPresent(actualVideo -> {
                        assertNotNull(actualVideo);
                        assertNotNull(actualVideo.getId());

                        assertEquals(aVideo.getId().getValue(), actualVideo.getId().getValue());
                        assertEquals(expectedTitle, actualVideo.getTitle());
                        assertEquals(expectedDescription, actualVideo.getDescription());
                        assertEquals(expectedLaunchedAt, actualVideo.getLaunchedAt().getValue());
                        assertEquals(expectedDuration, actualVideo.getDuration());
                        assertEquals(expectedReleaseStatus, actualVideo.getReleaseStatus());
                        assertEquals(expectedPublishingStatus, actualVideo.getPublishingStatus());
                        assertEquals(expectedRating, actualVideo.getRating());
                        assertEquals(expectedCategories, actualVideo.getCategories());
                        assertEquals(expectedGenres, actualVideo.getGenres());
                        assertEquals(expectedCastMembers, actualVideo.getCastMembers());
                        assertEquals(expectedVideo.name(), actualVideo.getVideo().name());
                        assertEquals(expectedTrailer.name(), actualVideo.getTrailer().name());
                        assertEquals(expectedBanner.name(), actualVideo.getBanner().name());
                        assertEquals(expectedThumbnail.name(), actualVideo.getThumbnail().name());
                        assertEquals(expectedThumbnailHalf.name(), actualVideo.getThumbnailHalf().name());
                    });
        }
    }

    @Nested
    @DisplayName("Get video by an invalid identifier")
    class GetVideoByInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_get_video_by_id_Then_should_return_empty() {
            // Given
            final var aBuilder = new Video.Builder(
                    Fixture.title(),
                    Fixture.Videos.description(),
                    Year.of(Fixture.year()),
                    Fixture.Videos.rating())
                    .duration(Fixture.duration())
                    .releaseStatus(Fixture.Videos.releaseStatus())
                    .publishingStatus(Fixture.Videos.publishingStatus())
                    .categories(Set.<CategoryID>of())
                    .genres(Set.<GenreID>of())
                    .castMembers(Set.<CastMemberID>of());

            defaultVideoGateway.create(
                    Video.newVideo(aBuilder));

            assertEquals(1, videoRepository.count());

            final var anId = VideoID.unique();

            // When
            final var actualVideo = defaultVideoGateway.findById(anId);

            // Then
            assertTrue(actualVideo.isEmpty());
        }
    }

    @Nested
    @DisplayName("List paginated videos")
    class ListPaginatedVideos {

        @Test
        void Given_empty_params_When_calls_findAll_Then_should_return_all_videos() {
            // Given
            mockVideos();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "";
            final var sort = "title";
            final var direction = "asc";
            final var expectedTotal = 4;

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction,
                    Set.of(),
                    Set.of(),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());
        }

        @Test
        void Given_empty_videos_table_When_calls_findAll_Then_should_return_empty_page() {
            // Given
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "";
            final var sort = "title";
            final var direction = "asc";
            final var expectedTotal = 0;

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction,
                    Set.of(),
                    Set.of(),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertTrue(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());
        }

        @Test
        void Given_a_valid_category_When_calls_findAll_Then_should_return_filtered_list() {
            mockVideos();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "";
            final var sort = "title";
            final var direction = "asc";
            final var expectedTotal = 2;

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction,
                    Set.of(),
                    Set.of(aulas.getId()),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());

            assertEquals("21.1 Implementação dos testes integrados do findAll",
                    actualResult.items().get(0).title());
            assertEquals("Aula de empreendedorismo", actualResult.items().get(1).title());
        }

        @Test
        void Given_a_valid_cast_member_When_calls_findAll_Then_should_return_filtered_list() {
            // Given
            mockVideos();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "";
            final var sort = "title";
            final var direction = "asc";
            final var expectedTotal = 2;

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction,
                    Set.of(wesley.getId()),
                    Set.of(),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());

            assertEquals("Aula de empreendedorismo", actualResult.items().get(0).title());
            assertEquals("System Design no Mercado Livre na prática", actualResult.items().get(1).title());
        }

        @Test
        void Given_a_valid_genre_When_calls_findAll_Then_should_return_filtered_list() {
            // Given
            mockVideos();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "";
            final var sort = "title";
            final var direction = "asc";
            final var expectedTotal = 1;

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction,
                    Set.of(),
                    Set.of(),
                    Set.of(business.getId())
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());

            assertEquals("Aula de empreendedorismo", actualResult.items().get(0).title());
        }

        @Test
        void Given_all_parameters_When_calls_findAll_Then_should_return_filtered_list() {
            // Given
            mockVideos();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "empreende";
            final var sort = "title";
            final var direction = "asc";
            final var expectedTotal = 1;

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction,
                    Set.of(wesley.getId()),
                    Set.of(aulas.getId()),
                    Set.of(business.getId())
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());

            assertEquals("Aula de empreendedorismo", actualResult.items().get(0).title());
        }

        @ParameterizedTest
        @CsvSource({
                "title,asc,0,10,4,4,21.1 Implementação dos testes integrados do findAll",
                "title,desc,0,10,4,4,System Design no Mercado Livre na prática",
                "createdAt,asc,0,10,4,4,System Design no Mercado Livre na prática",
                "createdAt,desc,0,10,4,4,Aula de empreendedorismo",
        })
        void Given_valid_sort_and_direction_When_calls_findAll_Then_should_return_sorted(
                final String expectedSort,
                final String expectedDirection,
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedName
        ) {
            // Given
            mockVideos();

            final var expectedTerms = "";

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection,
                    Set.of(),
                    Set.of(),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            assertEquals(expectedName, actualResult.items().get(0).title());
        }

        @ParameterizedTest
        @CsvSource({
                "system,0,10,1,1,System Design no Mercado Livre na prática",
                "microsser,0,10,1,1,Não cometa esses erros ao trabalhar com Microsserviços",
                "empreendedorismo,0,10,1,1,Aula de empreendedorismo",
                "21,0,10,1,1,21.1 Implementação dos testes integrados do findAll",
        })
        void Given_valid_terms_When_calls_findAll_Then_should_return_filtered(
                final String expectedTerms,
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedVideo
        ) {
            // Given
            mockVideos();
            final var sort = "title";
            final var direction = "asc";

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    sort,
                    direction,
                    Set.of(),
                    Set.of(),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            assertEquals(expectedVideo, actualResult.items().get(0).title());
        }

        @ParameterizedTest
        @CsvSource({
                "0,2,2,4,21.1 Implementação dos testes integrados do findAll;Aula de empreendedorismo",
                "1,2,2,4,Não cometa esses erros ao trabalhar com Microsserviços;System Design no Mercado Livre na prática",
        })
        void Given_valid_pages_When_calls_findAll_Then_should_return_paginated(
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedVideos
        ) {
            // Given
            mockVideos();

            final var expectedTerms = "";
            final var expectedSort = "title";
            final var expectedDirection = "asc";

            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection,
                    Set.of(),
                    Set.of(),
                    Set.of()
            );

            // When
            final var actualResult = defaultVideoGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            int index = 0;
            for (final var expectedTitle : expectedVideos.split(";")) {
                final var actualTitle = actualResult.items().get(index).title();
                assertEquals(expectedTitle, actualTitle);
                index++;
            }
        }
    }

    private void mockVideos() {
        defaultVideoGateway.create(
                Video.newVideo(new Video.Builder(
                        "System Design no Mercado Livre na prática",
                        Fixture.Videos.description(),
                        Year.of(Fixture.year()),
                        Fixture.Videos.rating())
                        .duration(Fixture.duration())
                        .releaseStatus(Fixture.Videos.releaseStatus())
                        .publishingStatus(Fixture.Videos.publishingStatus())
                        .categories(Set.of(lives.getId()))
                        .genres(Set.of(tech.getId()))
                        .castMembers(Set.of(wesley.getId(), gabriel.getId()))));
        defaultVideoGateway.create(
                Video.newVideo(new Video.Builder(
                        "Não cometa esses erros ao trabalhar com Microsserviços",
                        Fixture.Videos.description(),
                        Year.of(Fixture.year()),
                        Fixture.Videos.rating())
                        .duration(Fixture.duration())
                        .releaseStatus(Fixture.Videos.releaseStatus())
                        .publishingStatus(Fixture.Videos.publishingStatus())
                        .categories(Set.of())
                        .genres(Set.of())
                        .castMembers(Set.of())));
        defaultVideoGateway.create(
                Video.newVideo(new Video.Builder(
                        "21.1 Implementação dos testes integrados do findAll",
                        Fixture.Videos.description(),
                        Year.of(Fixture.year()),
                        Fixture.Videos.rating())
                        .duration(Fixture.duration())
                        .releaseStatus(Fixture.Videos.releaseStatus())
                        .publishingStatus(Fixture.Videos.publishingStatus())
                        .categories(Set.of(aulas.getId()))
                        .genres(Set.of(tech.getId()))
                        .castMembers(Set.of(gabriel.getId()))));
        defaultVideoGateway.create(
                Video.newVideo(new Video.Builder(
                        "Aula de empreendedorismo",
                        Fixture.Videos.description(),
                        Year.of(Fixture.year()),
                        Fixture.Videos.rating())
                        .duration(Fixture.duration())
                        .releaseStatus(Fixture.Videos.releaseStatus())
                        .publishingStatus(Fixture.Videos.publishingStatus())
                        .categories(Set.of(aulas.getId()))
                        .genres(Set.of(business.getId()))
                        .castMembers(Set.of(wesley.getId()))));
    }
}
