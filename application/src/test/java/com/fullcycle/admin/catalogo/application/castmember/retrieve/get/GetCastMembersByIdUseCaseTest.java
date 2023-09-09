package com.fullcycle.admin.catalogo.application.castmember.retrieve.get;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.application.UseCaseTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class GetCastMembersByIdUseCaseTest extends UseCaseTest {

    @Mock
    private CastMemberGateway castMemberGateway;

    @InjectMocks
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Nested
    @DisplayName("Get a cast member by id with a valid identifier")
    class GetCastMembersByIdWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_get_cast_member_by_id_Then_should_return_cast_member_output() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            when(castMemberGateway.findById(any()))
                    .thenReturn(Optional.of(CastMember.with(aCastMember)));

            // when
            final var actualOutput = getCastMemberByIdUseCase.execute(expectedId.getValue());

            // then
            assertNotNull(actualOutput);
            assertEquals(expectedId.getValue(), actualOutput.id());
            assertEquals(expectedName, actualOutput.name());
            assertEquals(expectedType, actualOutput.type());
            assertEquals(aCastMember.getCreatedAt(), actualOutput.createdAt());
            assertEquals(aCastMember.getUpdatedAt(), actualOutput.updatedAt());

            verify(castMemberGateway, times(1)).findById(expectedId);
        }
    }

    @Nested
    @DisplayName("Get a cast member by id with an invalid identifier")
    class GetCastMembersByIdWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_get_cast_member_by_id_Then_should_return_not_found() {
            // given
            final var expectedId = CastMemberID.from("123");
            final var expectedErrorMessage = "CastMember with ID %s was not found"
                    .formatted(expectedId.getValue());

            when(castMemberGateway.findById(expectedId))
                    .thenReturn(Optional.empty());

            // when
            Executable invalidMethodCall = () -> getCastMemberByIdUseCase.execute(expectedId.getValue());

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());

            verify(castMemberGateway, times(1)).findById(expectedId);
        }
    }
}
