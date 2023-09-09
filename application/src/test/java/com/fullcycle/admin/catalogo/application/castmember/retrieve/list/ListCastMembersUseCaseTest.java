package com.fullcycle.admin.catalogo.application.castmember.retrieve.list;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ListCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Nested
    @DisplayName("List cast members with a valid query")
    class ListWithAValidQuery {

        @Test
        void Given_a_valid_query_When_calls_list_cast_members_Then_should_return_a_non_empty_list_output() {
            // Given
            final var castMembers = List.of(
                    CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type()),
                    CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type()));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "A";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var expectedTotal = 2;
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination = new Pagination<>(
                    expectedPage,
                    expectedPerPage,
                    expectedTotal,
                    castMembers);

            when(castMemberGateway.findAll(any()))
                    .thenReturn(expectedPagination);

            final var expectedItems = castMembers.stream()
                    .map(CastMemberListOutput::from)
                    .toList();

            // when
            final var actualOutput = listCastMembersUseCase.execute(aQuery);

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedPage, actualOutput.currentPage());
            assertEquals(expectedPerPage, actualOutput.perPage());
            assertEquals(expectedTotal, actualOutput.total());
            assertEquals(expectedItems, actualOutput.items());

            verify(castMemberGateway, times(1)).findAll(aQuery);
        }
    }

    @Nested
    @DisplayName("List cast members with a valid query and has no result")
    class ListWithAValidQueryAndHasNoResult {

        @Test
        void Given_a_valid_query_When_calls_list_cast_members_and_has_no_result_Then_should_return_empty_list() {
            // given
            final var castMembers = List.<CastMember>of();

            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "Algo";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var expectedTotal = 0;
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedItems = List.<CastMemberListOutput>of();

            final var expectedPagination = new Pagination<>(
                    expectedPage,
                    expectedPerPage,
                    expectedTotal,
                    castMembers);

            when(castMemberGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCastMembersUseCase.execute(aQuery);
            // then
            assertNotNull(actualOutput);
            assertEquals(expectedPage, actualOutput.currentPage());
            assertEquals(expectedPerPage, actualOutput.perPage());
            assertEquals(expectedTotal, actualOutput.total());
            assertEquals(expectedItems, actualOutput.items());

            verify(castMemberGateway, times(1)).findAll(aQuery);
        }
    }

    @Nested
    @DisplayName("List cast members with a valid query and a generic error from gateway")
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
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            when(castMemberGateway.findAll(any()))
                    .thenThrow(new IllegalStateException(GATEWAY_ERROR));

            // when
            Executable invalidMethodCall = () -> listCastMembersUseCase.execute(aQuery);

            // then
            final var actualException = assertThrows(IllegalStateException.class, invalidMethodCall);
            assertNotNull(GATEWAY_ERROR, actualException.getMessage());

            verify(castMemberGateway, times(1)).findAll(aQuery);
        }
    }
}
