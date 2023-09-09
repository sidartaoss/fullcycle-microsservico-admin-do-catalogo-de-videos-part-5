package com.fullcycle.admin.catalogo.application.video.retrieve.list;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.video.VideoGateway;
import com.fullcycle.admin.catalogo.domain.video.VideoPreview;
import com.fullcycle.admin.catalogo.domain.video.VideoSearchQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ListVideosUseCaseTest extends UseCaseTest {

    @Mock
    private VideoGateway videoGateway;

    @InjectMocks
    private DefaultListVideosUseCase listVideosUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(videoGateway);
    }

    @Nested
    @DisplayName("List videos with a valid query")
    class ListWithAValidQuery {

        @Test
        void Given_a_valid_query_When_calls_list_videos_Then_should_return_a_non_empty_list_output() {
            // Given
            final var videos = List.of(
                    VideoPreview.from(Fixture.video()),
                    VideoPreview.from(Fixture.video()));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "A";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var expectedTotal = 2;
            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection,
                    Set.of(),
                    Set.of(),
                    Set.of());

            final var expectedPagination = new Pagination<>(
                    expectedPage,
                    expectedPerPage,
                    expectedTotal,
                    videos);

            when(videoGateway.findAll(any()))
                    .thenReturn(expectedPagination);

            final var expectedItems = videos.stream()
                    .map(VideoListOutput::from)
                    .toList();

            // when
            final var actualOutput = listVideosUseCase.execute(aQuery);

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedPage, actualOutput.currentPage());
            assertEquals(expectedPerPage, actualOutput.perPage());
            assertEquals(expectedTotal, actualOutput.total());
            assertEquals(expectedItems, actualOutput.items());

            verify(videoGateway, times(1)).findAll(aQuery);
        }
    }

    @Nested
    @DisplayName("List videos with a valid query and has no result")
    class ListWithAValidQueryAndHasNoResult {

        @Test
        void Given_a_valid_query_When_calls_list_videos_and_has_no_result_Then_should_return_empty_list() {
            // given
            final var videos = List.<VideoPreview>of();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "Algo";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var expectedTotal = 0;
            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection,
                    Set.of(),
                    Set.of(),
                    Set.of());

            final var expectedItems = List.<VideoListOutput>of();

            final var expectedPagination = new Pagination<>(
                    expectedPage,
                    expectedPerPage,
                    expectedTotal,
                    videos);

            when(videoGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listVideosUseCase.execute(aQuery);
            // then
            assertNotNull(actualOutput);
            assertEquals(expectedPage, actualOutput.currentPage());
            assertEquals(expectedPerPage, actualOutput.perPage());
            assertEquals(expectedTotal, actualOutput.total());
            assertEquals(expectedItems, actualOutput.items());

            verify(videoGateway, times(1)).findAll(aQuery);
        }
    }

    @Nested
    @DisplayName("List videos with a valid query and a generic error from gateway")
    class ListWithAValidQueryAndAGenericGatewayError {

        private static final String GATEWAY_ERROR = "Gateway error";

        @Test
        void Given_a_valid_query_When_a_generic_gateway_error_is_thrown_Then_should_return_an_error_message() {
            // given
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new VideoSearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection,
                    Set.of(),
                    Set.of(),
                    Set.of());

            when(videoGateway.findAll(any()))
                    .thenThrow(new IllegalStateException(GATEWAY_ERROR));

            // when
            Executable invalidMethodCall = () -> listVideosUseCase.execute(aQuery);

            // then
            final var actualException = assertThrows(IllegalStateException.class, invalidMethodCall);
            assertNotNull(GATEWAY_ERROR, actualException.getMessage());

            verify(videoGateway, times(1)).findAll(aQuery);
        }
    }
}
