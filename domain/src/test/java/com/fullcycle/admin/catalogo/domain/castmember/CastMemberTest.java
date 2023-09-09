package com.fullcycle.admin.catalogo.domain.castmember;

import com.fullcycle.admin.catalogo.domain.UnitTest;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CastMemberTest extends UnitTest {

    @DisplayName("Create a cast member with valid params")
    @Nested
    class CreateWithValidParams {

        @Test
        void Given_valid_params_When_calls_newCastMember_Then_should_instantiate_a_cast_member() {
            // given
            final var expectedName = "Vin Diesel";
            final var expectedType = CastMemberType.ACTOR;

            // when
            final var actualCastMember = CastMember.newCastMember(expectedName, expectedType);

            // then
            assertNotNull(actualCastMember);
            assertNotNull(actualCastMember.getId());
            assertEquals(expectedName, actualCastMember.getName());
            assertEquals(expectedType, actualCastMember.getType());
            assertNotNull(actualCastMember.getCreatedAt());
            assertNotNull(actualCastMember.getUpdatedAt());
            assertEquals(actualCastMember.getCreatedAt(), actualCastMember.getUpdatedAt());
        }
    }

    @DisplayName("Create a new cast member with invalid params")
    @Nested
    class CreateWithInvalidParams {

        @Test
        void Given_invalid_null_name_When_calls_newCastMember_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedType = CastMemberType.ACTOR;
            final var expectedErrorMessage = "'name' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> CastMember.newCastMember(null,
                    expectedType);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_empty_name_When_calls_newCastMember_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedType = CastMemberType.ACTOR;
            final String expectedName = " ";
            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> CastMember.newCastMember(expectedName, expectedType);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_name_with_length_greater_than_255_When_calls_newCastMember_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedType = CastMemberType.ACTOR;
            final String expectedName = """
                    Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se a estrutura atual da organização acarreta um processo de reformulação e modernização do orçamento setorial. A certificação de metodologias que nos auxiliam a lidar com a consulta aos diversos militantes facilita a criação de todos os recursos funcionais envolvidos.
                    """;
            final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> CastMember.newCastMember(expectedName, expectedType);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_null_type_When_calls_newCastMember_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedName = "Van Diesel";
            final var expectedErrorMessage = "'type' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> CastMember.newCastMember(expectedName, null);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }
    }

    @DisplayName("Update a cast member with valid params")
    @Nested
    class UpdateWithValidParams {

        @Test
        void Given_valid_castMember_When_calls_update_with_Then_should_receive_Ok() {
            // given
            final var expectedName = "Vin Diesel";
            final var expectedType = CastMemberType.ACTOR;

            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);
            assertNotNull(aCastMember);
            assertNotNull(aCastMember.getId());

            final var actualId = aCastMember.getId();
            final var updatedAt = aCastMember.getUpdatedAt();
            final var createdAt = aCastMember.getCreatedAt();

            // when
            final ThrowingSupplier<CastMember> validMethodCall = () -> aCastMember.update(expectedName, expectedType);

            // then
            final var actualCastMember = assertDoesNotThrow(validMethodCall);
            assertNotNull(actualCastMember);
            assertNotNull(actualCastMember.getId());

            assertEquals(actualId, actualCastMember.getId());
            assertEquals(expectedName, actualCastMember.getName());
            assertEquals(expectedType, actualCastMember.getType());
            assertEquals(createdAt, actualCastMember.getCreatedAt());
            assertTrue(actualCastMember.getUpdatedAt().isAfter(updatedAt));
        }
    }

    @DisplayName("Update a cast member with invalid params")
    @Nested
    class UpdateWithInvalidParams {

        @Test
        void Given_invalid_null_name_When_calls_update_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedType = CastMemberType.ACTOR;

            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);

            final var expectedErrorMessage = "'name' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> aCastMember.update(null,
                    expectedType);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_empty_name_When_calls_update_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedType = CastMemberType.ACTOR;
            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> aCastMember.update(" ", expectedType);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_name_with_length_greater_than_255_When_calls_update_Then_should_receive_an_error() {
            // given
            final String expectedName = """
                    Todas estas questões, devidamente ponderadas, levantam dúvidas sobre se a estrutura atual da organização acarreta um processo de reformulação e modernização do orçamento setorial. A certificação de metodologias que nos auxiliam a lidar com a consulta aos diversos militantes facilita a criação de todos os recursos funcionais envolvidos.
                    """;
            final var expectedType = CastMemberType.ACTOR;
            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);

            final var expectedErrorMessage = "'name' must be between 3 and 255 characters";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> aCastMember.update(expectedName, expectedType);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }

        @Test
        void Given_invalid_null_type_When_calls_update_and_validate_Then_should_receive_an_error() {
            // given
            final var expectedName = "Vin Diesel";
            final var aCastMember = CastMember.newCastMember("vin d", CastMemberType.DIRECTOR);

            final var expectedErrorMessage = "'type' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> aCastMember.update(expectedName, null);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
            assertEquals(expectedErrorCount, actualException.getErrors().size());
        }
    }
}
