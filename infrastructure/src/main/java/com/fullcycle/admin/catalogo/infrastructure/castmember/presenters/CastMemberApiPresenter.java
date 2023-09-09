package com.fullcycle.admin.catalogo.infrastructure.castmember.presenters;

import com.fullcycle.admin.catalogo.application.castmember.retrieve.get.GetCastMemberByIdOutput;
import com.fullcycle.admin.catalogo.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalogo.infrastructure.castmember.models.GetCastMemberByIdResponse;

public interface CastMemberApiPresenter {

    static GetCastMemberByIdResponse present(final GetCastMemberByIdOutput output) {
        return new GetCastMemberByIdResponse(
                output.id(),
                output.name(),
                output.type(),
                output.createdAt(),
                output.updatedAt()
        );
    }

    static CastMemberListResponse present(final CastMemberListOutput output) {
        return new CastMemberListResponse(
                output.id(),
                output.name(),
                output.type(),
                output.createdAt()
        );
    }
}
