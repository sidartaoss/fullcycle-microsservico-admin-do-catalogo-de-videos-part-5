package com.fullcycle.admin.catalogo.e2e.castmember;

import com.fullcycle.admin.catalogo.E2ETest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.e2e.MockDsl;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@E2ETest
@Testcontainers
public class CastMemberE2ETest implements MockDsl {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Override
    public MockMvc mvc() {
        return mockMvc;
    }

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        final var mappedPort = MYSQL_CONTAINER.getMappedPort(3306);
        System.out.printf("Container is running on port %s\n", mappedPort);
        registry.add("mysql.port", () -> mappedPort);
    }

    @Nested
    @DisplayName("Create cast member with valid values")
    class CreateWithValidValues {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_create_a_new_cast_member_with_valid_values() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();

            // When
            final var actualId = givenACastMember(expectedName, expectedType);

            // Then
            final var actualCastMember = retrieveACastMember(actualId);

            assertEquals(expectedName, actualCastMember.name());
            assertEquals(expectedType, actualCastMember.type());
            assertNotNull(actualCastMember.createdAt());
            assertNotNull(actualCastMember.updatedAt());
            assertEquals(actualCastMember.createdAt(), actualCastMember.updatedAt());
        }
    }

    @Nested
    @DisplayName("Create cast member with invalid values")
    class CreateWithInvalidValues {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_see_a_treated_error_by_creating_with_invalid_values()
                throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var expectedName = " ";
            final var expectedType = Fixture.CastMembers.type();
            final var expectedErrorMessage = "'name' should not be empty";

            // When
            final var actualResult = givenACastMemberResult(expectedName, expectedType);

            // Then
            actualResult
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Location", nullValue()))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
        }
    }

    @Nested
    @DisplayName("List cast members")
    class ListCastMembers {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_navigate_through_all_cast_members() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var aNameVinDiesel = "Vin Diesel";
            final var aNameQuentinTarantino = "Quentin Tarantino";
            final var aNameJasonMomoa = "Jason Momoa";

            givenACastMember(aNameVinDiesel, CastMemberType.ACTOR);
            givenACastMember(aNameQuentinTarantino, CastMemberType.DIRECTOR);
            givenACastMember(aNameJasonMomoa, CastMemberType.ACTOR);

            var expectedPage = 0;
            final var expectedPerPage = 1;
            final var expectedTotal = 3;

            // When & Then
            listCastMembers(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameJasonMomoa))));

            expectedPage = 1;
            listCastMembers(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameQuentinTarantino))));

            expectedPage = 2;
            listCastMembers(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameVinDiesel))));

            expectedPage = 3;
            listCastMembers(expectedPage, expectedPerPage)
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(0)));
        }

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_search_between_all_cast_members() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var aNameVinDiesel = "Vin Diesel";
            final var aNameQuentinTarantino = "Quentin Tarantino";
            final var aNameJasonMomoa = "Jason Momoa";

            givenACastMember(aNameVinDiesel, CastMemberType.ACTOR);
            givenACastMember(aNameQuentinTarantino, CastMemberType.DIRECTOR);
            givenACastMember(aNameJasonMomoa, CastMemberType.ACTOR);

            var expectedPage = 0;
            final var expectedPerPage = 1;
            final var expectedTotal = 1;

            // When & Then
            listCastMembers(expectedPage, expectedPerPage, "vin")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameVinDiesel))));
        }

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_sort_all_cast_members_by_name_desc() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var aNameVinDiesel = "Vin Diesel";
            final var aNameQuentinTarantino = "Quentin Tarantino";
            final var aNameJasonMomoa = "Jason Momoa";

            givenACastMember(aNameVinDiesel, CastMemberType.ACTOR);
            givenACastMember(aNameQuentinTarantino, CastMemberType.DIRECTOR);
            givenACastMember(aNameJasonMomoa, CastMemberType.ACTOR);

            var expectedPage = 0;
            final var expectedPerPage = 3;
            final var expectedTotal = 3;

            // When & Then
            listCastMembers(expectedPage, expectedPerPage, "", "name", "desc")
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", is(equalTo(expectedPage))))
                    .andExpect(jsonPath("$.per_page", is(equalTo(expectedPerPage))))
                    .andExpect(jsonPath("$.total", is(equalTo(expectedTotal))))
                    .andExpect(jsonPath("$.items", hasSize(expectedPerPage)))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aNameVinDiesel))))
                    .andExpect(jsonPath("$.items[1].name", is(equalTo(aNameQuentinTarantino))))
                    .andExpect(jsonPath("$.items[2].name", is(equalTo(aNameJasonMomoa))));
        }
    }

    @Nested
    @DisplayName("Get a cast member with valid identifier")
    class GetWithValidId {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_get_a_cast_member_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();

            givenACastMember(Fixture.name(), Fixture.CastMembers.type());
            givenACastMember(Fixture.name(), Fixture.CastMembers.type());
            final var actualId = givenACastMember(expectedName, expectedType);

            // When
            final var actualCastMember = retrieveACastMember(actualId);

            // Then
            assertEquals(expectedName, actualCastMember.name());
            assertEquals(expectedType, actualCastMember.type());
            assertNotNull(actualCastMember.createdAt());
            assertNotNull(actualCastMember.updatedAt());
        }
    }

    @Nested
    @DisplayName("Get a cast member with invalid identifier")
    class GetWithInvalidIdentifier {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_see_a_treated_error_by_getting_a_not_found_cast_member()
                throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            givenACastMember(Fixture.name(), Fixture.CastMembers.type());
            givenACastMember(Fixture.name(), Fixture.CastMembers.type());

            final var expectedId = "123";
            final var expectedErrorMessage = "CastMember with ID %s was not found"
                    .formatted(expectedId);

            // When & Then
            retrieveACastMemberResult(CastMemberID.from(expectedId))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        }
    }

    @Nested
    @DisplayName("Update a cast member with valid request")
    class UpdateWithValidRequest {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_update_a_cast_member_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var expectedName = "Vin Diesel";
            final var expectedType = CastMemberType.ACTOR;

            final var actualId = givenACastMember("Vin d", CastMemberType.DIRECTOR);

            final var aRequestBody = new UpdateCastMemberRequest(expectedName, expectedType);

            // when
            updateACastMember(actualId, aRequestBody)
                    .andExpect(status().isOk());

            // Then
            final var actualCastMember = retrieveACastMember(actualId);
            assertEquals(expectedName, actualCastMember.name());
            assertEquals(expectedType, actualCastMember.type());
            assertNotNull(actualCastMember.createdAt());
            assertNotNull(actualCastMember.updatedAt());
            assertNotEquals(actualCastMember.createdAt(), actualCastMember.updatedAt());
        }

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_see_a_treated_error_by_updating_a_cast_member_with_invalid_value()
                throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var expectedName = " ";
            final var expectedType = Fixture.CastMembers.type();

            final var expectedErrorMessage = "'name' should not be empty";

            givenACastMember(Fixture.name(), Fixture.CastMembers.type());

            final var actualId = givenACastMember("Vin d", CastMemberType.DIRECTOR);
            final var aRequestBody = new UpdateCastMemberRequest(expectedName, expectedType);

            // When & Then
            updateACastMember(actualId, aRequestBody)
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
        }
    }

    @Nested
    @DisplayName("Delete a cast member with valid identifier")
    class DeleteWithValidIdentifier {

        @Test
        void As_a_Catalog_Admin_I_Should_be_able_to_delete_a_cast_member_by_its_identifier() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();

            givenACastMember(expectedName, expectedType);
            final var actualId = givenACastMember(expectedName, expectedType);

            assertEquals(2, castMemberRepository.count());

            final var expectedErrorMessage = "CastMember with ID %s was not found"
                    .formatted(actualId.getValue());

            // When
            deleteACastMember(actualId)
                    .andExpect(status().isNoContent());

            // Then
            retrieveACastMemberResult(actualId)
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));

            assertEquals(1, castMemberRepository.count());
            assertFalse(castMemberRepository.existsById(actualId.getValue()));
        }
    }

    @Nested
    @DisplayName("Delete a cast member with invalid identifier")
    class DeleteWithInvalidIdentifier {

        @Test
        void As_a_Catalog_Admin_I_Should_not_see_an_error_by_deleting_a_non_existing_cast_member() throws Exception {
            assertTrue(MYSQL_CONTAINER.isRunning());
            assertEquals(0, castMemberRepository.count());

            // Given
            givenACastMember(Fixture.name(), Fixture.CastMembers.type());
            givenACastMember(Fixture.name(), Fixture.CastMembers.type());

            assertEquals(2, castMemberRepository.count());

            // When
            deleteACastMember(CastMemberID.from("123"))
                    .andExpect(status().isNoContent());

            // Then
            assertEquals(2, castMemberRepository.count());
        }
    }
}
