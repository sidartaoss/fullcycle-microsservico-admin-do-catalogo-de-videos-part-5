package com.fullcycle.admin.catalogo.infrastructure.video;

import com.fullcycle.admin.catalogo.domain.Identifier;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.configuration.annotations.VideoCreatedQueue;
import com.fullcycle.admin.catalogo.infrastructure.services.EventService;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.video.persistence.VideoRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.toStrings;
import static com.fullcycle.admin.catalogo.infrastructure.utils.SqlUtils.like;
import static com.fullcycle.admin.catalogo.infrastructure.utils.SqlUtils.upper;

@Component
public class DefaultVideoGateway implements VideoGateway {

    private final EventService eventService;
    private final VideoRepository videoRepository;

    public DefaultVideoGateway(
            final @VideoCreatedQueue EventService eventService,
            final VideoRepository videoRepository) {
        this.videoRepository = Objects.requireNonNull(videoRepository);
        this.eventService = Objects.requireNonNull(eventService);
    }

    @Transactional
    @Override
    public Video create(final Video aVideo) {
        return this.save(aVideo);
    }

    @Override
    public void deleteById(final VideoID anId) {
        final var aVideoId = anId.getValue();
        final var exists = this.videoRepository.existsById(aVideoId);
        if (exists) {
            this.videoRepository.deleteById(aVideoId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Video> findById(final VideoID anId) {
        return this.videoRepository.findById(anId.getValue())
                .map(VideoJpaEntity::toAggregate);
    }

    @Override
    public Video update(final Video aVideo) {
        return this.save(aVideo);
    }

    @Override
    public Pagination<VideoPreview> findAll(final VideoSearchQuery aQuery) {
        final var pageRequest = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );
        final var actualPage = this.videoRepository.findAll(
                like(upper(aQuery.terms())),
                toStrings(aQuery.castMembers(), Identifier::getValue),
                toStrings(aQuery.categories(), Identifier::getValue),
                toStrings(aQuery.genres(), Identifier::getValue),
                pageRequest
        );
        return new Pagination<>(
                actualPage.getNumber(),
                actualPage.getSize(),
                actualPage.getTotalElements(),
                actualPage.toList()
        );
    }

    private Video save(final Video aVideo) {
        final var result = this.videoRepository.save(VideoJpaEntity.from(aVideo))
                .toAggregate();
        aVideo.publishDomainEvents(this.eventService::send);
        return result;
    }
}
