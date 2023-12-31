package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ListCategoriesUseCaseTest extends UseCaseTest {

    /**
     * 1. Teste do caminho feliz
     * 2. Teste recuperar nenhuma categoria
     * 3. Teste simulando um erro genérico vindo do Gateway
     */

    @Mock
    CategoryGateway categoryGateway;

    @InjectMocks
    DefaultListCategoriesUseCase listCategoriesUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(categoryGateway);
    }

    @Nested
    @DisplayName("List categories with a valid query")
    class ListWithAValidQuery {

        @Test
        void Given_a_valid_query_When_calls_list_categories_Then_should_return_a_non_null_list_output() {
            // given
            final var categories = List.of(
                    Category.newCategory("Filmes", " "),
                    Category.newCategory("Séries", " "));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertNotNull(actualOutput);
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_Then_should_return_list_output_items_count() {
            // given
            final var categories = List.of(
                    Category.newCategory("Filmes", " "),
                    Category.newCategory("Séries", " "));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);
            final var expectedItemsCount = 2;

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedItemsCount, actualOutput.items().size());
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_Then_should_return_list_output_current_page() {
            // given
            final var categories = List.of(
                    Category.newCategory("Filmes", " "),
                    Category.newCategory("Séries", " "));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedPage, actualOutput.currentPage());
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_Then_should_return_list_output_per_page() {
            // given
            final var categories = List.of(
                    Category.newCategory("Filmes", " "),
                    Category.newCategory("Séries", " "));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualCategoryListOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedPerPage, actualCategoryListOutput.perPage());
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_Then_should_return_list_output_total() {
            // given
            final var categories = List.of(
                    Category.newCategory("Filmes", " "),
                    Category.newCategory("Séries", " "));
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(categories.size(), actualOutput.total());
        }
    }

    @Nested
    @DisplayName("List categories with a valid query and has no result")
    class ListWithAValidQueryAndHasNoResult {

        @Test
        void Given_a_valid_query_When_calls_list_categories_and_has_no_result_Then_should_return_non_null_list() {
            // given
            final var categories = List.<Category>of();
            final var expectedCategoriesSize = 0;
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, expectedCategoriesSize, categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertNotNull(actualOutput);
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_and_has_no_result_Then_should_return_items_count() {
            // given
            final var categories = List.<Category>of();
            final var expectedCategoriesSize = 0;
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, expectedCategoriesSize, categories);

            final var expectedItemsCount = 0;

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedItemsCount, actualOutput.items().size());
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_and_has_no_result_Then_should_return_current_page() {
            // given
            final var categories = List.<Category>of();
            final var expectedCategoriesSize = 0;
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, expectedCategoriesSize, categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedPage, actualOutput.currentPage());
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_and_has_no_result_Then_should_return_per_page() {
            // given
            final var categories = List.<Category>of();
            final var expectedCategoriesSize = 0;
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, expectedCategoriesSize, categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedPerPage, actualOutput.perPage());
        }

        @Test
        void Given_a_valid_query_When_calls_list_categories_and_has_no_result_Then_should_return_total() {
            // given
            final var categories = List.<Category>of();
            final var expectedCategoriesSize = 0;
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "createdAt";
            final var expectedDirection = "asc";
            final var aQuery = new SearchQuery(
                    expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

            final var expectedPagination =
                    new Pagination<>(expectedPage, expectedPerPage, expectedCategoriesSize, categories);

            when(categoryGateway.findAll(aQuery))
                    .thenReturn(expectedPagination);

            // when
            final var actualOutput = listCategoriesUseCase.execute(aQuery);
            // then
            assertEquals(expectedCategoriesSize, actualOutput.total());
        }
    }

    @Nested
    @DisplayName("List categories with a valid query and a generic error from gateway")
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

            when(categoryGateway.findAll(aQuery))
                    .thenThrow(new IllegalStateException(GATEWAY_ERROR));

            // when
            Executable invalidMethodCall = () -> listCategoriesUseCase.execute(aQuery);
            // then
            final var actualException = assertThrows(IllegalStateException.class, invalidMethodCall);
            assertNotNull(GATEWAY_ERROR, actualException.getMessage());
        }
    }
}
