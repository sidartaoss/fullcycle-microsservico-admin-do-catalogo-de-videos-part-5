package com.fullcycle.admin.catalogo.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalogo.ApiTest;
import com.fullcycle.admin.catalogo.ControllerTest;
import com.fullcycle.admin.catalogo.domain.Fixture;
import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberOutput;
import com.fullcycle.admin.catalogo.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.delete.DefaultDeleteCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.DefaultGetCastMemberByIdUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.DefaultListCastMembersUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.DefaultUpdateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberOutput;
import com.fullcycle.admin.catalogo.domain.castmember.CastMember;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.validation.handler.Notification;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DefaultDeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private DefaultGetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private DefaultListCastMembersUseCase listCastMembersUseCase;

    @MockBean
    private DefaultUpdateCastMemberUseCase updateCastMemberUseCase;

    @Nested
    @DisplayName("Create a cast member with valid request")
    class CreateWithValidRequest {

        @Test
        void Given_a_valid_request_When_calls_create_cast_member_Then_should_return_its_identifier()
                throws Exception {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();
            final var aRequest =
                    new CreateCastMemberRequest(expectedName, expectedType);

            when(createCastMemberUseCase.execute(any()))
                    .thenReturn(CreateCastMemberOutput.from(expectedId));

            final var mockMvcRequest = post("/cast_members")
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", "/cast_members/" + expectedId.getValue()))
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));
            verify(createCastMemberUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedType, command.type())
                    ));
        }
    }

    @Nested
    @DisplayName("Create a cast member with invalid request")
    class CreateWithInvalidRequest {

        @Test
        void Given_an_invalid_request_When_calls_create_cast_member_Then_should_return_domain_exception()
                throws Exception {
            // Given
            final var expectedName = " ";
            final var expectedType = Fixture.CastMembers.type();
            final var aRequest =
                    new CreateCastMemberRequest(expectedName, expectedType);

            final var expectedErrorMessage = "'name' should not be empty";

            when(createCastMemberUseCase.execute(any()))
                    .thenThrow(new NotificationException("Error",
                            Notification.create(new Error(expectedErrorMessage))));

            final var request = post("/cast_members")
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Location", nullValue()))
                    .andExpect(jsonPath("$.errors", hasSize(1)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
            verify(createCastMemberUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedType, command.type())
                    ));
        }
    }

    @Nested
    @DisplayName("Get a cast member with valid identifier")
    class GetCastMemberWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_find_by_id_Then_should_return_cast_member() throws Exception {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId().getValue();

            when(getCastMemberByIdUseCase.execute(any()))
                    .thenReturn(GetCastMemberByIdOutput.from(aCastMember));

            final var request = get("/cast_members/{id}", expectedId)
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId)))
                    .andExpect(jsonPath("$.name", equalTo(expectedName)))
                    .andExpect(jsonPath("$.type", equalTo(expectedType.toString())))
                    .andExpect(jsonPath("$.created_at", equalTo(aCastMember.getCreatedAt().toString())))
                    .andExpect(jsonPath("$.updated_at", equalTo(aCastMember.getUpdatedAt().toString())));
            verify(getCastMemberByIdUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("Get a cast member with invalid identifier")
    class GetCastMemberWithInvalidIdentifier {

        @Test
        void Given_an_invalid_identifier_When_calls_find_by_id_Then_should_return_not_found() throws Exception {
            // Given
            final var expectedId = "123";
            final var expectedErrorMessage = "CastMember with ID %s was not found".formatted(expectedId);

            final var request = get("/cast_members/{id}", expectedId)
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .contentType(MediaType.APPLICATION_JSON);

            when(getCastMemberByIdUseCase.execute(expectedId))
                    .thenThrow(NotFoundException.with(
                            CastMember.class,
                            CastMemberID.from(expectedId)));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
            verify(getCastMemberByIdUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("Update a cast member with valid request")
    class UpdateWithValidRequest {

        @Test
        void Given_a_valid_request_When_calls_update_cast_member_Then_should_return_cast_member_updated()
                throws Exception {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aCastMember = CastMember.newCastMember(expectedName, expectedType);
            final var expectedId = aCastMember.getId();
            final var aRequest =
                    new UpdateCastMemberRequest(expectedName, expectedType);

            when(updateCastMemberUseCase.execute(any()))
                    .thenReturn(UpdateCastMemberOutput.from(expectedId));

            final var mockMvcRequest = put("/cast_members/{id}", expectedId.getValue())
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.id", equalTo(expectedId.getValue())));
            verify(updateCastMemberUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedId.getValue(), command.id())
                                    && Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedType, command.type())
                    ));
        }
    }

    @Nested
    @DisplayName("Update cast member with an invalid name")
    class UpdateWithInvalidName {

        @Test
        void Given_an_invalid_name_When_calls_update_cast_member_Then_should_return_domain_exception()
                throws Exception {
            // Given
            final var expectedName = " ";
            final var expectedType = CastMemberType.ACTOR;
            final var aCastMember = CastMember.newCastMember("Vin d", CastMemberType.DIRECTOR);
            final var expectedId = aCastMember.getId();
            final var aRequest =
                    new UpdateCastMemberRequest(expectedName, expectedType);

            final var expectedErrorMessage = "'name' should not be empty";
            final var expectedErrorCount = 1;

            when(updateCastMemberUseCase.execute(any()))
                    .thenThrow(new NotificationException("Error",
                            Notification.create(new Error(expectedErrorMessage))));

            final var mockMvcRequest = put("/cast_members/{id}", expectedId.getValue())
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(mockMvcRequest)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.errors", hasSize(expectedErrorCount)))
                    .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
            verify(updateCastMemberUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedId.getValue(), command.id())
                                    && Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedType, command.type())
                    ));
        }
    }

    @Nested
    @DisplayName("Update a genre with invalid identifier")
    class UpdateWithInvalidIdentifier {

        @Test
        void Given_an_invalid_id_When_calls_update_cast_member_Then_should_return_not_found_exception()
                throws Exception {
            // Given
            final var expectedName = Fixture.name();
            final var expectedType = Fixture.CastMembers.type();
            final var aRequest =
                    new UpdateCastMemberRequest(expectedName, expectedType);

            final var expectedId = "not-found";
            final var expectedErrorMessage = "%s with ID %s was not found"
                    .formatted(CastMember.class.getSimpleName(), expectedId);

            when(updateCastMemberUseCase.execute(any()))
                    .thenThrow(NotFoundException.with(
                            CastMember.class, CastMemberID.from(expectedId)));

            final var request = put("/cast_members/{id}", expectedId)
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(aRequest));

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNotFound())
                    .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
            verify(updateCastMemberUseCase, times(1))
                    .execute(argThat(command ->
                            Objects.equals(expectedId, command.id())
                                    && Objects.equals(expectedName, command.name())
                                    && Objects.equals(expectedType, command.type())
                    ));
        }
    }

    @Nested
    @DisplayName("Delete a cast member with valid identifier")
    class DeleteCastMemberWithValidIdentifier {

        @Test
        void Given_a_valid_identifier_When_calls_delete_cast_member_Then_should_return_no_content() throws Exception {
            // Given
            final var expectedId = "123";

            doNothing().when(deleteCastMemberUseCase)
                    .execute(any());

            final var request = delete("/cast_members/{id}", expectedId)
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isNoContent());
            verify(deleteCastMemberUseCase, times(1)).execute(any());
        }
    }

    @Nested
    @DisplayName("List cast members with valid params")
    class ListCastMembersWithValidParams {

        @Test
        void Given_a_valid_params_When_calls_list_cast_members_Then_should_return_cast_members()
                throws Exception {
            // Given
            final var aCastMember = CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type());
            final var expectedPage = 1;
            final var expectedPerPage = 20;
            final var expectedTerms = "Alg";
            final var expectedSort = "type";
            final var expectedDirection = "desc";

            final var expectedItemsCount = 1;
            final var expectedTotal = 1;

            final var expectedItems = List.of(CastMemberListOutput.from(aCastMember));

            when(listCastMembersUseCase.execute(any()))
                    .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

            final var request = get("/cast_members")
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .queryParam("page", String.valueOf(expectedPage))
                    .queryParam("perPage", String.valueOf(expectedPerPage))
                    .queryParam("sort", expectedSort)
                    .queryParam("dir", expectedDirection)
                    .queryParam("search", expectedTerms)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                    .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                    .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                    .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                    .andExpect(jsonPath("$.items[0].id", is(equalTo(aCastMember.getId().getValue()))))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aCastMember.getName()))))
                    .andExpect(jsonPath("$.items[0].type", is(equalTo(aCastMember.getType().toString()))))
                    .andExpect(jsonPath("$.items[0].created_at", is(equalTo(aCastMember.getCreatedAt().toString()))));
            verify(listCastMembersUseCase, times(1)).execute(argThat(query ->
                    Objects.equals(expectedPage, query.page())
                            && Objects.equals(expectedPerPage, query.perPage())
                            && Objects.equals(expectedDirection, query.direction())
                            && Objects.equals(expectedSort, query.sort())
                            && Objects.equals(expectedTerms, query.terms())
            ));
        }

        @Test
        void Given_a_empty_params_When_calls_list_cast_members_Then_should_return_defaults()
                throws Exception {
            // Given
            final var aCastMember = CastMember.newCastMember(Fixture.name(), Fixture.CastMembers.type());
            final var expectedPage = 0;
            final var expectedPerPage = 10;
            final var expectedTerms = "";
            final var expectedSort = "name";
            final var expectedDirection = "asc";

            final var expectedItemsCount = 1;
            final var expectedTotal = 1;

            final var expectedItems = List.of(CastMemberListOutput.from(aCastMember));

            when(listCastMembersUseCase.execute(any()))
                    .thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));

            final var request = get("/cast_members")
                    .with(ApiTest.CAST_MEMBERS_JWT)
                    .queryParam("page", String.valueOf(expectedPage))
                    .queryParam("perPage", String.valueOf(expectedPerPage))
                    .queryParam("sort", expectedSort)
                    .queryParam("dir", expectedDirection)
                    .queryParam("search", expectedTerms)
                    .accept(MediaType.APPLICATION_JSON);

            // When
            final var response = mockMvc.perform(request)
                    .andDo(print());

            // Then
            response
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.current_page", equalTo(expectedPage)))
                    .andExpect(jsonPath("$.per_page", equalTo(expectedPerPage)))
                    .andExpect(jsonPath("$.total", equalTo(expectedTotal)))
                    .andExpect(jsonPath("$.items", hasSize(expectedItemsCount)))
                    .andExpect(jsonPath("$.items[0].id", is(equalTo(aCastMember.getId().getValue()))))
                    .andExpect(jsonPath("$.items[0].name", is(equalTo(aCastMember.getName()))))
                    .andExpect(jsonPath("$.items[0].type", is(equalTo(aCastMember.getType().toString()))))
                    .andExpect(jsonPath("$.items[0].created_at", is(equalTo(aCastMember.getCreatedAt().toString()))));
            verify(listCastMembersUseCase, times(1)).execute(argThat(query ->
                    Objects.equals(expectedPage, query.page())
                            && Objects.equals(expectedPerPage, query.perPage())
                            && Objects.equals(expectedDirection, query.direction())
                            && Objects.equals(expectedSort, query.sort())
                            && Objects.equals(expectedTerms, query.terms())
            ));
        }
    }
}
