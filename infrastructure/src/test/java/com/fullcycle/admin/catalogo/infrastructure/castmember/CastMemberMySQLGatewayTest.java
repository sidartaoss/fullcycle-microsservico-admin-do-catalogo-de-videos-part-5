package com.fullcycle.admin.catalogo.infrastructure.castmember;

import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@MySQLGatewayTest
class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Nested
    @DisplayName("Create with a valid cast member")
    class CreateWithValidCastMember {

        @Test
        void Given_a_valid_cast_member_When_calls_create_cast_member_Then_should_persist_it() {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();

            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            assertEquals(0, castMemberRepository.count());

            // When
            final var actualCastMember = castMemberGateway.create(CastMember.with(aCastMember));

            // Then
            assertEquals(1, castMemberRepository.count());

            assertEquals(expectedId, actualCastMember.getId());
            assertEquals(expectedName, actualCastMember.getName());
            assertEquals(expectedType, actualCastMember.getType());
            assertEquals(aCastMember.getCreatedAt(), actualCastMember.getCreatedAt());
            assertEquals(aCastMember.getUpdatedAt(), actualCastMember.getUpdatedAt());

            castMemberRepository.findById(expectedId.getValue())
                    .ifPresent(persistedCastMember -> {

                        assertEquals(expectedName, persistedCastMember.getName());
                        assertEquals(expectedType, persistedCastMember.getType());
                        assertEquals(aCastMember.getCreatedAt(), persistedCastMember.getCreatedAt());
                        assertEquals(aCastMember.getUpdatedAt(), persistedCastMember.getUpdatedAt());
                    });
        }
    }

    @Nested
    @DisplayName("Update with a valid cast member")
    class UpdateWithValidCastMember {

        @Test
        void Given_a_valid_cast_member_When_calls_update_Then_should_save_it() {
            // Given
            final var expectedName = "Vin Diesel";
            final var expectedType = CastMemberType.ACTOR;

            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);

            final var expectedId = aCastMember.getId();

            assertEquals(0, castMemberRepository.count());

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));

            assertEquals(1, castMemberRepository.count());

            castMemberRepository.findById(expectedId.getValue())
                    .ifPresent(actualInvalidEntity -> {
                        assertEquals("vin d", actualInvalidEntity.getName());
                        assertEquals(CastMemberType.DIRECTOR, actualInvalidEntity.getType());
                    });

            final var anUpdatedCastMember = CastMember.with(aCastMember)
                    .update(expectedName, expectedType);

            // When
            final var actualCastMember = castMemberGateway.update(anUpdatedCastMember);

            // Then
            assertEquals(1, castMemberRepository.count());

            assertEquals(expectedId, actualCastMember.getId());
            assertEquals(expectedName, actualCastMember.getName());
            assertEquals(expectedType, actualCastMember.getType());
            assertEquals(anUpdatedCastMember.getCreatedAt(), actualCastMember.getCreatedAt());
            assertTrue(actualCastMember.getUpdatedAt().isAfter(aCastMember.getUpdatedAt()));

            castMemberGateway.findById(expectedId)
                    .ifPresent(persistedCastMember -> {

                        assertEquals(expectedName, persistedCastMember.getName());
                        assertEquals(expectedType, persistedCastMember.getType());
                        assertEquals(actualCastMember.getCreatedAt(), persistedCastMember.getCreatedAt());
                        assertEquals(actualCastMember.getUpdatedAt(), persistedCastMember.getUpdatedAt());
                    });
        }

        @Test
        void Given_two_cast_members_and_one_is_persisted_When_calls_exists_by_ids_Then_should_return_persisted_id() {
            // Given
            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);

            final var expectedItems = 1;
            final var expectedId = aCastMember.getId();

            assertEquals(0, castMemberRepository.count());

            castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aCastMember));

            // When
            final var actualCastMember = castMemberGateway.existsByIds(
                    List.of(CastMemberID.from("123"), expectedId));

            // Then
            assertEquals(expectedItems, actualCastMember.size());
            assertEquals(expectedId.getValue(), actualCastMember.get(0).getValue());
        }
    }

    @Nested
    @DisplayName("Delete with a valid cast member identifier")
    class DeleteWithValidCastMemberIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_delete_cast_member_Then_should_delete_it() {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            assertEquals(0, castMemberRepository.count());
            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            // When
            castMemberGateway.deleteById(expectedId);

            // Then
            assertEquals(0, castMemberRepository.count());
        }
    }

    @Nested
    @DisplayName("Delete cast member with an invalid identifier")
    class DeleteWithInvalidCastMemberIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_delete_Then_should_return_ok() {
            // Given
            final var aCastMember = CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type());

            assertEquals(0, castMemberRepository.count());
            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            final var expectedId = CastMemberID.from("invalid");

            // When
            castMemberGateway.deleteById(expectedId);

            // Then
            assertEquals(1, castMemberRepository.count());
        }
    }

    @Nested
    @DisplayName("Get cast member by a valid identifier")
    class GetCastMemberByValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_find_by_id_Then_should_return_cast_member() {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            assertEquals(0, castMemberRepository.count());

            castMemberRepository
                    .save(CastMemberJpaEntity.from(aCastMember));

            assertEquals(1, castMemberRepository.count());

            // When & Then
            castMemberGateway.findById(expectedId)
                    .ifPresent(actualCastMember -> {

                        assertEquals(expectedName, actualCastMember.getName());
                        assertEquals(expectedType, actualCastMember.getType());
                        assertEquals(aCastMember.getCreatedAt(), actualCastMember.getCreatedAt());
                        assertEquals(aCastMember.getUpdatedAt(), actualCastMember.getUpdatedAt());
                    });
            assertEquals(1, castMemberRepository.count());
        }

        @Test
        void Given_a_non_stored_valid_identifier_When_calls_find_by_id_Then_should_return_empty() {
            // Given
            final var aCastMember = CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type());

            assertEquals(0, castMemberRepository.count());
            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            final var nonStoredValidCastMemberId = CastMemberID.from("empty");

            // When
            final var actualCategory = castMemberGateway.findById(nonStoredValidCastMemberId);

            // Then
            assertTrue(actualCategory.isEmpty());
        }
    }

    @Nested
    @DisplayName("List paginated cast members")
    class ListPaginatedCastMembers {

        @Test
        void Given_empty_cast_members_table_When_calls_findAll_Then_should_return_empty_page() {
            // Given
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var terms = "";
            final var sort = "name";
            final var direction = "asc";
            final var expectedTotal = 0;

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    terms,
                    sort,
                    direction
            );

            // When
            final var actualResult = castMemberGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertTrue(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedTotal, actualResult.items().size());
        }

        @ParameterizedTest
        @CsvSource({
                "vin,0,10,1,1,Vin Diesel",
                "taran,0,10,1,1,Quentin Tarantino",
                "jas,0,10,1,1,Jason Momoa",
                "har,0,10,1,1,Kit Harrington",
                "MAR,0,10,1,1,Martin Scorsese",
        })
        void Given_valid_terms_When_calls_findAll_Then_should_return_filtered(
                final String expectedTerms,
                final int expectedPage,
                final int expectedPerPage,
                final int expectedItemsCount,
                final long expectedTotal,
                final String expectedName
        ) {
            // Given
            mockCastMembers();
            final var sort = "name";
            final var direction = "asc";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    sort,
                    direction
            );

            // When
            final var actualResult = castMemberGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            assertEquals(expectedName, actualResult.items().get(0).getName());
        }

        @ParameterizedTest
        @CsvSource({
                "name,asc,0,10,5,5,Jason Momoa",
                "name,desc,0,10,5,5,Vin Diesel",
                "createdAt,asc,0,10,5,5,Kit Harrington",
                "createdAt,desc,0,10,5,5,Martin Scorsese",
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
            mockCastMembers();
            final var expectedTerms = "";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection
            );

            // When
            final var actualResult = castMemberGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            assertEquals(expectedName, actualResult.items().get(0).getName());
        }

        @ParameterizedTest
        @CsvSource({
                "0,2,5,2,Jason Momoa;Kit Harrington",
                "1,2,5,2,Martin Scorsese;Quentin Tarantino",
                "2,2,5,1,Vin Diesel",
        })
        void Given_valid_pages_When_calls_findAll_Then_should_return_paginated(
                final int expectedPage,
                final int expectedPerPage,
                final long expectedTotal,
                final int expectedItemsCount,
                final String expectedCastMembers
        ) {
            // Given
            mockCastMembers();
            final var expectedTerms = "";
            final var expectedSort = "name";
            final var expectedDirection = "asc";

            final var aQuery = new SearchQuery(
                    expectedPage,
                    expectedPerPage,
                    expectedTerms,
                    expectedSort,
                    expectedDirection
            );

            // When
            final var actualResult = castMemberGateway.findAll(aQuery);

            // Then
            assertNotNull(actualResult);
            assertFalse(actualResult.items().isEmpty());

            assertEquals(expectedPage, actualResult.currentPage());
            assertEquals(expectedPerPage, actualResult.perPage());
            assertEquals(expectedTotal, actualResult.total());
            assertEquals(expectedItemsCount, actualResult.items().size());
            int index = 0;
            for (final var expectedName : expectedCastMembers.split(";")) {
                final var actualName = actualResult.items().get(index).getName();
                assertEquals(expectedName, actualName);
                index++;
            }
        }
    }

    private void mockCastMembers() {
        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(CastMember.newCastMember("Kit Harrington", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newCastMember("Vin Diesel", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newCastMember("Quentin Tarantino", CastMemberType.DIRECTOR)),
                CastMemberJpaEntity.from(CastMember.newCastMember("Jason Momoa", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newCastMember("Martin Scorsese", CastMemberType.DIRECTOR))
        ));
    }
}
