//package com.fullcycle.admin.catalogo.application.video.create;
//
//import com.fullcycle.admin.catalogo.IntegrationTest;
//import com.fullcycle.admin.catalogo.domain.Fixture;
//import com.fullcycle.admin.catalogo.domain.Identifier;
//import com.fullcycle.admin.catalogo.domain.resource.Resource;
//import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
//import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.SpyBean;
//
//import java.util.ArrayList;
//import java.util.Objects;
//import java.util.Set;
//
//import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.AdditionalAnswers.returnsFirstArg;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.*;
//
//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//@IntegrationTest
//public class CreateVideoUseCaseIT {
//
//    @Autowired
//    private CreateVideoUseCase createVideoUseCase;
//
//    @Autowired
//    private VideoRepository videoRepository;
//
//    @SpyBean
//    private VideoGateway videoGateway;
//
//    @Nested
//    @DisplayName("Create a video with valid command values")
//    class CreateWithValidCommandValues {
//
//        @Test
//        void Given_a_valid_command_When_calls_create_video_Then_should_return_a_video_id() {
//            // given
//            final var expectedTitle = Fixture.title();
//            final var expectedDescription = Fixture.Videos.description();
//            final var expectedLaunchedAt = Fixture.year();
//            final var expectedDuration = Fixture.duration();
//            final var expectedReleaseStatus = Fixture.Videos.releaseStatus();
//            final var expectedPublishingStatus = Fixture.Videos.publishingStatus();
//            final var expectedRating = Fixture.Videos.rating();
//            final var expectedCategories = Set.of(Fixture.Categories.aulas().getId());
//            final var expectedGenres = Set.of(Fixture.Genres.tech().getId());
//            final var expectedCastMembers = Set.of(
//                    Fixture.CastMembers.wesley().getId(),
//                    Fixture.CastMembers.gabriel().getId()
//            );
//
//            final Resource expectedVideo = Fixture.Videos.resource(Resource.Type.VIDEO);
//            final Resource expectedTrailer = Fixture.Videos.resource(Resource.Type.TRAILER);
//            final Resource expectedBanner = Fixture.Videos.resource(Resource.Type.BANNER);
//            final Resource expectedThumbnail = Fixture.Videos.resource(Resource.Type.THUMBNAIL);
//            final Resource expectedThumbnailHalf = Fixture.Videos.resource(Resource.Type.THUMBNAIL_HALF);
//
//            final var aCommand =
//                    CreateVideoCommand.with(
//                            expectedTitle,
//                            expectedDescription,
//                            expectedLaunchedAt,
//                            expectedDuration,
//                            expectedReleaseStatus,
//                            expectedPublishingStatus,
//                            expectedRating.getName(),
//                            mapTo(expectedCategories, Identifier::getValue),
//                            mapTo(expectedGenres, Identifier::getValue),
//                            mapTo(expectedCastMembers, Identifier::getValue),
//                            expectedVideo,
//                            expectedTrailer,
//                            expectedBanner,
//                            expectedThumbnail,
//                            expectedThumbnailHalf
//                    );
//
//            when(categoryGateway.existsByIds(any()))
//                    .thenReturn(new ArrayList<>(expectedCategories));
//
//            when(genreGateway.existsByIds(any()))
//                    .thenReturn(new ArrayList<>(expectedGenres));
//
//            when(castMemberGateway.existsByIds(any()))
//                    .thenReturn(new ArrayList<>(expectedCastMembers));
//
//            mockImageMedia();
//            mockAudioVideoMedia();
//
//            when(videoGateway.create(any()))
//                    .thenAnswer(returnsFirstArg());
//
//            // when
//            final CreateVideoOutput actualOutput = createVideoUseCase.execute(aCommand);
//
//            // then
//            assertNotNull(actualOutput);
//            assertNotNull(actualOutput.id());
//
//            verify(videoGateway, times(1)).create(argThat(aVideo ->
//                    Objects.equals(actualOutput.id(), aVideo.getId().getValue())
//                            && Objects.equals(expectedTitle, aVideo.getTitle())
//                            && Objects.equals(expectedDescription, aVideo.getDescription())
//                            && Objects.equals(expectedLaunchedAt, aVideo.getLaunchedAt().getValue())
//                            && Objects.equals(expectedDuration, aVideo.getDuration())
//                            && Objects.equals(expectedReleaseStatus, aVideo.getReleaseStatus())
//                            && Objects.equals(expectedPublishingStatus, aVideo.getPublishingStatus())
//                            && Objects.equals(expectedRating, aVideo.getRating())
//                            && Objects.equals(expectedCategories, aVideo.getCategories())
//                            && Objects.equals(expectedGenres, aVideo.getGenres())
//                            && Objects.equals(expectedCastMembers, aVideo.getCastMembers())
//                            && Objects.nonNull(aVideo.getVideo())
//                            && Objects.equals(expectedVideo.name(), aVideo.getVideo().name())
//                            && Objects.nonNull(aVideo.getTrailer())
//                            && Objects.equals(expectedTrailer.name(), aVideo.getTrailer().name())
//                            && Objects.nonNull(aVideo.getBanner())
//                            && Objects.equals(expectedBanner.name(), aVideo.getBanner().name())
//                            && Objects.nonNull(aVideo.getThumbnail())
//                            && Objects.equals(expectedThumbnail.name(), aVideo.getThumbnail().name())
//                            && Objects.nonNull(aVideo.getThumbnailHalf())
//                            && Objects.equals(expectedThumbnailHalf.name(), aVideo.getThumbnailHalf().name())
//            ));
//        }
//    }
//}
