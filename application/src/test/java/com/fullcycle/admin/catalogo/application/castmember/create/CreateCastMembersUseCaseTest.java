package com.fullcycle.admin.catalogo.application.castmember.create;

import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.Fixture;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CreateCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;


    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Nested
    @DisplayName("Create a cast member with valid command")
    class CreateWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_create_cast_member_Then_should_return_a_genre_id() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();

            final var aCommand =
                    CreateCastMemberCommand.with(expectedName, expectedType);

            when(castMemberGateway.create(any()))
                    .thenAnswer(returnsFirstArg());

            // when
            final var actualOutput = createCastMemberUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            verify(castMemberGateway, times(1)).create(argThat(aCastMember ->
                    Objects.nonNull(aCastMember.getId())
                            && Objects.equals(expectedName, aCastMember.getName())
                            && Objects.equals(expectedType, aCastMember.getType())
                            && Objects.nonNull(aCastMember.getCreatedAt())
                            && Objects.nonNull(aCastMember.getUpdatedAt()))
            );
        }
    }

    @Nested
    @DisplayName("Create a cast member with invalid command")
    class CreateWithInvalidCommand {

        @Test
        void Given_an_invalid_empty_name_When_calls_create_cast_member_Then_should_return_a_domain_exception() {
            // given
            final var expectedName = " ";
            final var expectedType = Fixture.CastMembers.type();

            final var aCommand =
                    CreateCastMemberCommand.with(expectedName, expectedType);

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> createCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, never()).create(any());
        }

        @Test
        void Given_an_invalid_null_name_When_calls_create_cast_member_Then_should_return_a_domain_exception() {
            // given
            final var expectedType = Fixture.CastMembers.type();

            final var aCommand =
                    CreateCastMemberCommand.with(null, expectedType);

            final var expectedErrorMessage = "'name' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> createCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, never()).create(any());
        }

        @Test
        void Given_an_invalid_null_type_When_calls_create_cast_member_Then_should_return_a_domain_exception() {
            // given
            final var expectedName = Fixture.name();

            final var aCommand =
                    CreateCastMemberCommand.with(expectedName, null);

            final var expectedErrorMessage = "'type' should not be null";
            final var expectedErrorCount = 1;

            // when
            Executable invalidMethodCall = () -> createCastMemberUseCase.execute(aCommand);

            // then
            final var actualException = Assertions.assertThrows(NotificationException.class, invalidMethodCall);

            assertNotNull(actualException);
            assertEquals(expectedErrorCount, actualException.getErrors().size());
            assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

            verify(castMemberGateway, never()).create(any());
        }
    }
}
