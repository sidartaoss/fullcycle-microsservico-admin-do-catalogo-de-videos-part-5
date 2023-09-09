package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberCommand;
import com.fullcycle.admin.catalogo.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.delete.DeleteCastMemberUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.ListCastMembersUseCase;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberCommand;
import com.fullcycle.admin.catalogo.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalogo.infrastructure.api.CastMemberAPI;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.GetCastMemberByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.UpdateCastMemberRequest;
import com.fullcycle.admin.catalogo.infrastructure.castmember.presenters.CastMemberApiPresenter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Objects;

@RestController
public class CastMemberController implements CastMemberAPI {

    private final CreateCastMemberUseCase createCastMemberUseCase;
    private final GetCastMemberByIdUseCase getCastMemberByIdUseCase;
    private final UpdateCastMemberUseCase updateCastMemberUseCase;
    private final DeleteCastMemberUseCase deleteCastMemberUseCase;
    private final ListCastMembersUseCase listCastMembersUseCase;

    public CastMemberController(
            final CreateCastMemberUseCase createCastMemberUseCase,
            final GetCastMemberByIdUseCase getCastMemberByIdUseCase,
            final UpdateCastMemberUseCase updateCastMemberUseCase,
            final DeleteCastMemberUseCase deleteCastMemberUseCase,
            final ListCastMembersUseCase listCastMembersUseCase) {
        this.createCastMemberUseCase = Objects.requireNonNull(createCastMemberUseCase);
        this.getCastMemberByIdUseCase = Objects.requireNonNull(getCastMemberByIdUseCase);
        this.updateCastMemberUseCase = Objects.requireNonNull(updateCastMemberUseCase);
        this.deleteCastMemberUseCase = Objects.requireNonNull(deleteCastMemberUseCase);
        this.listCastMembersUseCase = Objects.requireNonNull(listCastMembersUseCase);
    }

    @Override
    public ResponseEntity<?> create(
            final CreateCastMemberRequest aRequest) {
        final var aName = aRequest.name();
        final var aType = aRequest.type();
        final var aCommand = CreateCastMemberCommand.with(aName, aType);
        final var output = this.createCastMemberUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/cast_members/%s".formatted(output.id())))
                .body(output);
    }

    @Override
    public Pagination<CastMemberListResponse> list(
            final String aSearch,
            final int aPage,
            final int aPerPage,
            final String aSort,
            final String aDirection) {
        final var aQuery = new SearchQuery(aPage, aPerPage, aSearch, aSort, aDirection);
        return listCastMembersUseCase.execute(aQuery)
                .map(CastMemberApiPresenter::present);
    }

    @Override
    public GetCastMemberByIdResponse getById(final String anId) {
        final var output = this.getCastMemberByIdUseCase.execute(anId);
        return CastMemberApiPresenter.present(output);
    }

    @Override
    public ResponseEntity<?> update(
            final String anId,
            final UpdateCastMemberRequest aRequest) {
        final var aName = aRequest.name();
        final var aType = aRequest.type();
        final var aCommand = UpdateCastMemberCommand.with(anId, aName, aType);
        final var output = this.updateCastMemberUseCase.execute(aCommand);
        return ResponseEntity.ok(output);
    }

    @Override
    public void delete(final String anId) {
        this.deleteCastMemberUseCase.execute(anId);
    }
}
