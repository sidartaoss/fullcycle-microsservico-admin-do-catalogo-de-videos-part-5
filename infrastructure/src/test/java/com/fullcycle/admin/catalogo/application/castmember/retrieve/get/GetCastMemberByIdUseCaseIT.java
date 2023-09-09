package com.fullcycle.admin.catalogo.application.castmember.retrieve.get;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@IntegrationTest
public class GetCastMemberByIdUseCaseIT {

    @Autowired
    private GetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Nested
    @DisplayName("Get a cast member by id with a valid identifier")
    class GetCastMemberByIdWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_get_cast_member_by_id_Then_should_return_it() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

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
    class GetCastMemberByIdWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_get_cast_member_by_id_Then_should_return_not_found() {
            // given
            final var expectedId = CastMemberID.from("123");
            final var expectedErrorMessage = "CastMember with ID %s was not found"
                    .formatted(expectedId.getValue());

            // when
            Executable invalidMethodCall = () -> getCastMemberByIdUseCase.execute(expectedId.getValue());

            // then
            final var actualException = assertThrows(NotFoundException.class, invalidMethodCall);
            assertEquals(expectedErrorMessage, actualException.getMessage());

            verify(castMemberGateway, times(1)).findById(expectedId);
        }
    }
}
