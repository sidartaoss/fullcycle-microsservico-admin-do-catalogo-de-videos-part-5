package com.fullcycle.admin.catalogo.application.castmember.create;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoEncoderCompleted;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
public class CreateCastMemberUseCaseIT {

    @Autowired
    private CreateCastMemberUseCase createCastMemberUseCase;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Nested
    @DisplayName("Create a cast member with valid command")
    class CreateWithValidCommand {

        @Test
        void Given_a_valid_command_When_calls_create_cast_member_Then_should_return_a_cast_member_id() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();

            final var aCommand =
                    CreateCastMemberCommand.with(expectedName, expectedType);

            // when
            final var actualOutput = createCastMemberUseCase.execute(aCommand);

            // then
            assertNotNull(actualOutput);
            assertNotNull(actualOutput.id());

            castMemberRepository.findById(actualOutput.id())
                    .ifPresent(actualCastMember -> {
                        assertEquals(expectedName, actualCastMember.getName());
                        assertEquals(expectedType, actualCastMember.getType());
                        assertNotNull(actualCastMember.getCreatedAt());
                        assertNotNull(actualCastMember.getUpdatedAt());
                    });
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
