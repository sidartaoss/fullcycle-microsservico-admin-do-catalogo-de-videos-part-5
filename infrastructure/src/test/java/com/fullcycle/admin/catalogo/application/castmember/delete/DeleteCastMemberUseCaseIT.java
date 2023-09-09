package com.fullcycle.admin.catalogo.application.castmember.delete;

import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberGateway;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
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
public class DeleteCastMemberUseCaseIT {

    @Autowired
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @SpyBean
    private CastMemberGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Nested
    @DisplayName("Delete a cast member with valid identifier")
    class DeleteWithValidIdentifier {

        private static final String GATEWAY_ERROR = "Gateway error";

        @Test
        void Given_a_valid_identifier_When_calls_delete_cast_member_Then_should_delete_it() {
            // given
            final var aCastMember = CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type());
            final var aCastMemberTwo = CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type());
            final var expectedId = aCastMember.getId();

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            castMemberRepository.save(CastMemberJpaEntity.from(aCastMemberTwo));
            assertEquals(2, castMemberRepository.count());

            // when
            Executable validMethodCall = () -> deleteCastMemberUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);
            assertEquals(1, castMemberRepository.count());
            assertFalse(castMemberRepository.existsById(expectedId.getValue()));
            assertTrue(castMemberRepository.existsById(aCastMemberTwo.getId().getValue()));

            verify(castMemberGateway, times(1)).deleteById(expectedId);
        }

        @Test
        void Given_a_valid_identifier_When_calls_delete_and_gateway_throws_some_error_Then_should_return_exception() {
            // given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();

            castMemberRepository.save(CastMemberJpaEntity.from(aCastMember));
            assertEquals(1, castMemberRepository.count());

            doThrow(new IllegalStateException(GATEWAY_ERROR))
                    .when(castMemberGateway).deleteById(any());

            // when
            Executable invalidMethodCall = () -> deleteCastMemberUseCase.execute(expectedId.getValue());

            // then
            assertThrows(IllegalStateException.class, invalidMethodCall);
            assertEquals(1, castMemberRepository.count());

            verify(castMemberGateway, times(1)).deleteById(expectedId);
        }
    }

    @Nested
    @DisplayName("Delete a cast member with an invalid identifier")
    class DeleteWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_delete_cast_member_Then_should_return_Ok() {
            // given
            castMemberRepository.saveAndFlush(
                    CastMemberJpaEntity.from(
                            CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type())));

            assertEquals(1, castMemberRepository.count());

            final var expectedId = CastMemberID.from("invalid");

            // when
            Executable validMethodCall = () -> deleteCastMemberUseCase.execute(expectedId.getValue());

            // then
            assertDoesNotThrow(validMethodCall);
            assertEquals(1, castMemberRepository.count());

            verify(castMemberGateway, times(1)).deleteById(expectedId);
        }
    }
}
