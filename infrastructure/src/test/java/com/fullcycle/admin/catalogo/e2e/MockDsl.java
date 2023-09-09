package com.fullcycle.admin.catalogo.e2e;

import com.fullcycle.admin.catalogo.ApiTest;
import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberType;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.GetCastMemberByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.CreateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.category.models.GetCategoryByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.category.models.UpdateCategoryRequest;
import com.fullcycle.admin.catalogo.infrastructure.configuration.json.Json;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.GetGenreByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.genre.models.UpdateGenreRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.function.Function;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public interface MockDsl {

    MockMvc mvc();

    default ResultActions deleteACategory(final CategoryID anId) throws Exception {
        return this.delete("/categories/", anId);
    }

    default ResultActions deleteAGenre(final GenreID anId) throws Exception {
        return this.delete("/genres/", anId);
    }

    default ResultActions deleteACastMember(final CastMemberID anId) throws Exception {
        return this.delete("/cast_members/", anId);
    }

    default GetCategoryByIdResponse retrieveACategory(final CategoryID anId) throws Exception {
        return this.retrieve("/categories/", anId, GetCategoryByIdResponse.class);
    }

    default ResultActions updateACategory(
            final CategoryID anId,
            final UpdateCategoryRequest aRequest) throws Exception {
        return this.update("/categories/", anId, aRequest);
    }

    default ResultActions activateACategory(
            final CategoryID anId) throws Exception {
        return this.update("/categories/%s/active", anId);
    }

    default ResultActions deactivateACategory(
            final CategoryID anId) throws Exception {
        return this.update("/categories/%s/inactive", anId);
    }

    default ResultActions updateAGenre(
            final GenreID anId,
            final UpdateGenreRequest aRequest) throws Exception {
        return this.update("/genres/", anId, aRequest);
    }

    default ResultActions updateACastMember(
            final CastMemberID anId,
            final UpdateCastMemberRequest aRequest) throws Exception {
        return this.update("/cast_members/", anId, aRequest);
    }

    default ResultActions activateAGenre(
            final Identifier anId) throws Exception {
        return this.update("/genres/%s/active", anId);
    }

    default ResultActions deactivateAGenre(
            final GenreID anId) throws Exception {
        return this.update("/genres/%s/inactive", anId);
    }

    default GetGenreByIdResponse retrieveAGenre(final GenreID anId) throws Exception {
        return this.retrieve("/genres/", anId, GetGenreByIdResponse.class);
    }

    default GetCastMemberByIdResponse retrieveACastMember(final CastMemberID anId) throws Exception {
        return this.retrieve("/cast_members/", anId, GetCastMemberByIdResponse.class);
    }

    default ResultActions retrieveACastMemberResult(final CastMemberID anId) throws Exception {
        return this.retrieveResult("/cast_members/", anId);
    }

    default ResultActions listCategories(
            final int page,
            final int perPage) throws Exception {
        return this.listCategories(page, perPage, "", "", "");
    }

    default ResultActions listCategories(
            final int page,
            final int perPage,
            final String search) throws Exception {
        return this.listCategories(page, perPage, search, "", "");
    }

    default ResultActions listCategories(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        return this.list("/categories", page, perPage, search, sort, direction);
    }

    default ResultActions listGenres(
            final int page,
            final int perPage) throws Exception {
        return this.listGenres(page, perPage, "", "", "");
    }

    default ResultActions listCastMembers(
            final int page,
            final int perPage) throws Exception {
        return this.listCastMembers(page, perPage, "", "", "");
    }

    default ResultActions listGenres(
            final int page,
            final int perPage,
            final String search) throws Exception {
        return this.listGenres(page, perPage, search, "", "");
    }

    default ResultActions listCastMembers(
            final int page,
            final int perPage,
            final String search) throws Exception {
        return this.listCastMembers(page, perPage, search, "", "");
    }

    default ResultActions listGenres(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        return this.list("/genres", page, perPage, search, sort, direction);
    }

    default ResultActions listCastMembers(
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        return this.list("/cast_members", page, perPage, search, sort, direction);
    }

    private ResultActions list(
            final String url,
            final int page,
            final int perPage,
            final String search,
            final String sort,
            final String direction) throws Exception {
        final var aRequest = get(url)
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", direction)
                .contentType(MediaType.APPLICATION_JSON)
                .with(ApiTest.ADMIN_JWT);
        return this.mvc().perform(aRequest);
    }

    default CategoryID givenACategory(final String aName, final String aDescription) throws Exception {
        final var aRequestBody = new CreateCategoryRequest(aName, aDescription);
        final var actualId = this.given("/categories", aRequestBody);
        return CategoryID.from(actualId);
    }

    default GenreID givenAGenre(final String aName, final List<CategoryID> aCategories) throws Exception {
        final var aRequestBody = new CreateGenreRequest(aName, mapTo(aCategories, CategoryID::getValue));
        final var actualId = this.given("/genres", aRequestBody);
        return GenreID.from(actualId);
    }

    default CastMemberID givenACastMember(final String aName, final CastMemberType aType) throws Exception {
        final var aRequestBody = new CreateCastMemberRequest(aName, aType);
        final var actualId = this.given("/cast_members", aRequestBody);
        return CastMemberID.from(actualId);
    }

    default ResultActions givenACastMemberResult(final String aName, final CastMemberType aType) throws Exception {
        final var aRequestBody = new CreateCastMemberRequest(aName, aType);
        return this.givenResult("/cast_members", aRequestBody);
    }

    default <A, D> List<D> mapTo(final List<A> actual, final Function<A, D> mapper) {
        return actual.stream()
                .map(mapper)
                .toList();
    }

    private String given(final String url, final Object body) throws Exception {
        final var aRequest = post(url)
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        final var actualId = this.mvc().perform(aRequest)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("%s/".formatted(url), "");

        return actualId;
    }

    private ResultActions givenResult(final String url, final Object body) throws Exception {
        final var aRequest = post(url)
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(body));

        return this.mvc().perform(aRequest);
    }

    private <T> T retrieve(
            final String url,
            final Identifier anId,
            final Class<T> clazz) throws Exception {
        final var aRequest = get(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        final var json = this.mvc().perform(aRequest)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString();

        return Json.readValue(json, clazz);
    }

    private ResultActions retrieveResult(final String url, final Identifier anId) throws Exception {
        final var aRequest = get(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8);

        return this.mvc().perform(aRequest);
    }

    private ResultActions delete(
            final String url,
            final Identifier anId) throws Exception {
        final var aRequest = MockMvcRequestBuilders.delete(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private ResultActions update(
            final String url,
            final Identifier anId) throws Exception {
        final var aRequest = MockMvcRequestBuilders.put(url.formatted(anId.getValue()))
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON);

        return this.mvc().perform(aRequest);
    }

    private ResultActions update(
            final String url,
            final Identifier anId,
            final Object aRequestBody) throws Exception {
        final var aRequest = MockMvcRequestBuilders.put(url + anId.getValue())
                .with(ApiTest.ADMIN_JWT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(aRequestBody));

        return this.mvc().perform(aRequest);
    }
}
