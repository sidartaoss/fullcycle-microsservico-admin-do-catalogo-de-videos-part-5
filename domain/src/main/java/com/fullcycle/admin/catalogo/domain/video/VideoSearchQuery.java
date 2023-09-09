package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;

import java.util.Collections;
import java.util.Set;

public record VideoSearchQuery(
        int page,
        int perPage,
        String terms,
        String sort,
        String direction,
        Set<CastMemberID> castMembers,
        Set<CategoryID> categories,
        Set<GenreID> genres
) {

    @Override
    public Set<CastMemberID> castMembers() {
        return castMembers == null ? Collections.emptySet() : Collections.unmodifiableSet(castMembers);
    }

    @Override
    public Set<CategoryID> categories() {
        return categories == null ? Collections.emptySet() : Collections.unmodifiableSet(categories);
    }

    @Override
    public Set<GenreID> genres() {
        return genres == null ? Collections.emptySet() : Collections.unmodifiableSet(genres);
    }
}
