package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import com.fullcycle.admin.catalogo.domain.video.VideoPreview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface VideoRepository extends JpaRepository<VideoJpaEntity, String> {

    @Query("""
            select distinct new com.fullcycle.admin.catalogo.domain.video.VideoPreview(
                v.id as id,
                v.title as title,
                v.description as description,
                v.createdAt as createdAt,
                v.updatedAt as updatedAt
            )
            from Video v
                left join v.castMembers members
                left join v.categories categories
                left join v.genres genres
            where
                ( :terms = '' or UPPER(v.title) like :terms )
            and
                ( :castMembers = '' or members.id.castMemberId in :castMembers )
            and
                ( :categories = '' or categories.id.categoryId in :categories )
            and
                ( :genres = '' or genres.id.genreId in :genres )
            """)
    Page<VideoPreview> findAll(
            @Param("terms") String terms,
            @Param("castMembers") String castMembers,
            @Param("categories") String categories,
            @Param("genres") String genres,
            Pageable page
    );
}
