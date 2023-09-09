package com.fullcycle.admin.catalogo.application.castmember.update;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UpdateCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;


    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

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

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            expectedName,
                            expectedType);

            when(castMemberGateway.findById(any()))
                    .thenReturn(Optional.of(CastMember.with(aCastMember)));

            when(castMemberGateway.update(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = updateCastMemberUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());

            verify(castMemberGateway, times(1)).findById(expectedId);

            verify(castMemberGateway, times(1)).update(argThat(anUpdatedCastMember ->
                    Objects.equals(expectedId, anUpdatedCastMember.getId())
                            && Objects.equals(expectedName, anUpdatedCastMember.getName())
                            && Objects.equals(expectedType, anUpdatedCastMember.getType())
                            && Objects.equals(aCastMember.getCreatedAt(), anUpdatedCastMember.getCreatedAt())
                            && anUpdatedCastMember.getUpdatedAt().isAfter(aCastMember.getUpdatedAt()))
            );
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

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            " ",
                            expectedType);

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            when(castMemberGateway.findById(any()))
                    .thenReturn(Optional.of(CastMember.with(aCastMember)));

            // when
            Executable invalidMethodCall = () -> updateCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, times(1)).findById(expectedId);
            verify(castMemberGateway, never()).update(any());
        }

        @Test
        void Given_an_invalid_type_When_calls_update_cast_member_Then_should_return_notification_exception() {
            // given
            final var expectedName = Fixture.name();
            final var aCastMember = CastMember.newCastMember("van d", CastMemberType.DIRECTOR);
            final var expectedId = aCastMember.getId();

            final var aCommand =
                    UpdateCastMemberCommand.with(
                            expectedId.getValue(),
                            expectedName,
                            null);

            final var expectedErrorMessage = "'type' should not be null";
            final var expectedErrorCount = 1;

            when(castMemberGateway.findById(any()))
                    .thenReturn(Optional.of(CastMember.with(aCastMember)));

            // when
            Executable invalidMethodCall = () -> updateCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotificationException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, times(1)).findById(expectedId);
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

            when(castMemberGateway.findById(any()))
                    .thenReturn(Optional.empty());

            // when
            Executable invalidMethodCall = () -> updateCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertNotNull(actualException);
            assertEquals(expectedErrorMessage, actualException.getMessage());

            verify(castMemberGateway, times(1)).findById(expectedId);
            verify(castMemberGateway, never()).update(any());
        }
    }
}
