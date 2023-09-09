package com.fullcycle.admin.catalogo.application.castmember.delete;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DeleteCastMembersUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Nested
    @DisplayName("Delete a cast member with valid identifier")
    class DeleteWithValidIdentifier {

        private static final String GATEWAY_ERROR = "Gateway error";

        @Test
        void Given_a_valid_identifier_When_calls_delete_cast_member_Then_should_delete_cast_member() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            doNothing().when(castMemberGateway)
                    .deleteById(any());

            // when
            Executable validMethodCall = () -> deleteCastMemberUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);

            verify(castMemberGateway, times(1)).deleteById(expectedId);
        }

        @Test
        void Given_a_valid_identifier_When_calls_delete_and_gateway_throws_some_error_Then_should_return_exception() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            doThrow(new IllegalStateException(GATEWAY_ERROR))
                    .when(castMemberGateway).deleteById(any());

            // when
            Executable invalidMethodCall = () -> deleteCastMemberUseCase.execute(expectedId.getValue());

            // then
            assertThrows(IllegalStateException.class, invalidMethodCall);

            verify(castMemberGateway, times(1)).deleteById(expectedId);
        }
    }

    @Nested
    @DisplayName("Delete a cast member with an invalid identifier")
    class DeleteWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_delete_cast_member_Then_should_return_Ok() {
            // given
            final var expectedId = CastMemberID.from("invalid");

            doNothing().when(castMemberGateway)
                    .deleteById(any());

            // when
            Executable validMethodCall = () -> deleteCastMemberUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);

            verify(castMemberGateway, times(1)).deleteById(expectedId);
        }
    }
}
