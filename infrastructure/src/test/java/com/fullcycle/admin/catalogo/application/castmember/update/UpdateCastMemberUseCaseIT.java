package com.fullcycle.admin.catalogo.application.castmember.update;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
public class UpdateCastMemberUseCaseIT {

    @Autowired
    private UpdateCastMemberUseCase updateCastMemberUseCase;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Nested
    @DisplayName("Update a cast member with valid command")
    class UpdateWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_update_cast_member_Then_should_return_identifier() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = CastMemberType.ACTOR;

            final var aCastMember = CastMember.newCastMember("van d", CastMemberType.DIRECTOR);
            final var expectedId = aCastMember.getId();

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            expectedName,
                            expectedType);

            // when
            final var actualOutput = updateCastMemberUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());

            castMemberRepository.findById(actualOutput.id())
                    .ifPresent(actualPersistedCastMember -> {
                        assertEquals(expectedName, actualPersistedCastMember.getName());
                        assertEquals(expectedType, actualPersistedCastMember.getType());
                        assertEquals(aCastMember.getCreatedAt(), actualPersistedCastMember.getCreatedAt());
                        assertTrue(actualPersistedCastMember.getUpdatedAt().isAfter(aCastMember.getUpdatedAt()));
                    });
            verify(castMemberGateway, times(1)).findById(any());
            verify(castMemberGateway, times(1)).update(any());
        }
    }

    @Nested
    @DisplayName("Update a cast member with invalid command")
    class UpdateWithInvalidCommand {

        @Test
        void Given_an_invalid_name_When_calls_update_cast_member_Then_should_return_notification_exception() {
            // given
            final var expectedType = CastMemberType.ACTOR;
            final var aCastMember = CastMember.newCastMember("van d", CastMemberType.DIRECTOR);
            final var expectedId = aCastMember.getId();

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            " ",
                            expectedType);

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> updateCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, times(1)).findById(any());
            verify(castMemberGateway, never()).update(any());
        }

        @Test
        void Given_an_invalid_type_When_calls_update_cast_member_Then_should_return_notification_exception() {
            // given
            final var expectedName = Fixture.name();
            final var aCastMember = CastMember.newCastMember("van d", CastMemberType.DIRECTOR);
            final var expectedId = aCastMember.getId();

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            expectedName,
                            null);

            final var expectedErrorMessage = "'type' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> updateCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, times(1)).findById(any());
            verify(castMemberGateway, never()).update(any());
        }

        @Test
        void Given_an_invalid_id_When_calls_update_cast_member_Then_should_return_not_found_exception() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = CastMemberType.ACTOR;

            final var expectedId = CastMemberID.from("123");

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            expectedName,
                            expectedType);

            final var expectedErrorMessage = "CastMember with ID %s was not found"
                    .formatted(expectedId.getValue());

            // when
            Executable invalidMethodCall = () -> updateCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorMessage, actualException.getMessage());

            verify(castMemberGateway, times(1)).findById(any());
            verify(castMemberGateway, never()).update(any());
        }
    }
}
