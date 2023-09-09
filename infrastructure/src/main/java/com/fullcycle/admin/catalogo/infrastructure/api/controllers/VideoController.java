package com.fullcycle.admin.catalogo.infrastructure.api.controllers;

import com.fullcycle.admin.catalogo.application.video.create.CreateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.create.CreateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.delete.DeleteVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaCommand;
import com.fullcycle.admin.catalogo.application.video.media.get.GetMediaUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.get.GetVideoByIdUseCase;
import com.fullcycle.admin.catalogo.application.video.retrieve.list.ListVideosUseCase;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoCommand;
import com.fullcycle.admin.catalogo.application.video.update.UpdateVideoUseCase;
import com.fullcycle.admin.catalogo.application.video.upload.UploadMediaCommand;
import com.fullcycle.admin.catalogo.application.video.upload.UploadMediaUseCase;
import com.fullcycle.admin.catalogo.domain.castmember.CastMemberID;
import com.fullcycle.admin.catalogo.domain.category.CategoryID;
import com.fullcycle.admin.catalogo.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalogo.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalogo.domain.genre.GenreID;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.validation.Error;
import com.fullcycle.admin.catalogo.domain.video.*;
import com.fullcycle.admin.catalogo.infrastructure.api.VideoAPI;
import com.fullcycle.admin.catalogo.infrastructure.utils.HashingUtils;
import com.fullcycle.admin.catalogo.infrastructure.video.models.CreateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.GetVideoByIdResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.models.UpdateVideoRequest;
import com.fullcycle.admin.catalogo.infrastructure.video.models.VideoListResponse;
import com.fullcycle.admin.catalogo.infrastructure.video.presenters.VideoApiPresenter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

import static com.fullcycle.admin.catalogo.domain.utils.CollectionUtils.mapTo;

@RestController
public class VideoController implements VideoAPI {

    private final CreateVideoUseCase createVideoUseCase;
    private final GetVideoByIdUseCase getVideoByIdUseCase;
    private final UpdateVideoUseCase updateVideoUseCase;
    private final DeleteVideoUseCase deleteVideoUseCase;
    private final ListVideosUseCase listVideosUseCase;
    private final GetMediaUseCase getMediaUseCase;
    private final UploadMediaUseCase uploadMediaUseCase;

    public VideoController(
            final CreateVideoUseCase createVideoUseCase,
            final GetVideoByIdUseCase getVideoByIdUseCase,
            final UpdateVideoUseCase updateVideoUseCase,
            final DeleteVideoUseCase deleteVideoUseCase,
            final ListVideosUseCase listVideosUseCase,
            final GetMediaUseCase getMediaUseCase,
            final UploadMediaUseCase uploadMediaUseCase) {
        this.createVideoUseCase = Objects.requireNonNull(createVideoUseCase);
        this.getVideoByIdUseCase = Objects.requireNonNull(getVideoByIdUseCase);
        this.updateVideoUseCase = Objects.requireNonNull(updateVideoUseCase);
        this.deleteVideoUseCase = Objects.requireNonNull(deleteVideoUseCase);
        this.listVideosUseCase = Objects.requireNonNull(listVideosUseCase);
        this.getMediaUseCase = Objects.requireNonNull(getMediaUseCase);
        this.uploadMediaUseCase = Objects.requireNonNull(uploadMediaUseCase);
    }

    @Override
    public ResponseEntity<?> createFull(
            final String aTitle,
            final String aDescription,
            final Integer anYearLaunched,
            final Double aDuration,
            final Boolean anOpened,
            final Boolean aPublished,
            final String aRating,
            final Set<String> aCategories,
            final Set<String> aCastMembers,
            final Set<String> aGenres,
            final MultipartFile aVideoFile,
            final MultipartFile aTrailerFile,
            final MultipartFile aBannerFile,
            final MultipartFile aThumbnailFile,
            final MultipartFile aThumbnailHalfFile) {
        final var aCommand = CreateVideoCommand.with(
                aTitle,
                aDescription,
                anYearLaunched,
                aDuration,
                releaseStatusOf(anOpened),
                publishingStatusOf(aPublished),
                aRating,
                aCategories,
                aGenres,
                aCastMembers,
                resourceOf(aVideoFile),
                resourceOf(aTrailerFile),
                resourceOf(aBannerFile),
                resourceOf(aThumbnailFile),
                resourceOf(aThumbnailHalfFile)
        );
        final var output = this.createVideoUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/videos/%s".formatted(output.id())))
                .body(output);
    }

    @Override
    public ResponseEntity<?> createPartial(final CreateVideoRequest aRequest) {
        final var aCommand = CreateVideoCommand.with(
                aRequest.title(),
                aRequest.description(),
                aRequest.yearLaunched(),
                aRequest.duration(),
                releaseStatusOf(aRequest.opened()),
                publishingStatusOf(aRequest.published()),
                aRequest.rating(),
                aRequest.categories(),
                aRequest.genres(),
                aRequest.castMembers()
        );
        final var output = this.createVideoUseCase.execute(aCommand);
        return ResponseEntity.created(URI.create("/videos/%s".formatted(output.id())))
                .body(output);
    }

    @Override
    public GetVideoByIdResponse getById(final String anId) {
        return VideoApiPresenter.present(this
                .getVideoByIdUseCase.execute(anId));
    }

    @Override
    public ResponseEntity<?> update(final String anId, final UpdateVideoRequest aRequest) {
        final var aCommand = UpdateVideoCommand.with(
                anId,
                aRequest.title(),
                aRequest.description(),
                aRequest.yearLaunched(),
                aRequest.duration(),
                releaseStatusOf(aRequest.opened()),
                publishingStatusOf(aRequest.published()),
                aRequest.rating(),
                aRequest.categories(),
                aRequest.genres(),
                aRequest.castMembers()
        );
        final var output = this.updateVideoUseCase.execute(aCommand);
        return ResponseEntity.ok(output);
    }

    @Override
    public void delete(final String anId) {
        this.deleteVideoUseCase.execute(anId);
    }

    @Override
    public Pagination<VideoListResponse> list(
            final String aSearch,
            final int aPage,
            final int aPerPage,
            final String aSort,
            final String aDirection,
            final Set<String> aCastMembers,
            final Set<String> aCategories,
            final Set<String> aGenres) {
        final var aQuery = new VideoSearchQuery(
                aPage,
                aPerPage,
                aSearch,
                aSort,
                aDirection,
                mapTo(aCastMembers, CastMemberID::from),
                mapTo(aCategories, CategoryID::from),
                mapTo(aGenres, GenreID::from));
        return this.listVideosUseCase.execute(aQuery)
                .map(VideoApiPresenter::present);
    }

    @Override
    public ResponseEntity<byte[]> getMediaByType(final String anId, final String aType) {
        final var aCommand = GetMediaCommand.with(anId, aType);
        final var aMedia = this.getMediaUseCase.execute(aCommand);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(aMedia.contentType()))
                .contentLength(aMedia.content().length)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=%s".formatted(aMedia.name()))
                .body(aMedia.content());
    }

    @Override
    public ResponseEntity<?> uploadMediaByType(
            final String aVideoId, final String aType, final MultipartFile aMedia) {
        final var aVideoMediaType = VideoMediaType.of(aType)
                .orElseThrow(() -> NotificationException.with(new Error("Invalid %s for VideoMediaType"
                        .formatted(aType))));
        final var aVideoResource = VideoResource.with(resourceOf(aMedia), aVideoMediaType);
        final var aCommand =
                UploadMediaCommand.with(aVideoId, aVideoResource);
        final var output = this.uploadMediaUseCase.execute(aCommand);
        return ResponseEntity
                .created(URI.create("/videos/%s/medias/%s".formatted(aVideoId, aType)))
                .body(output);
    }

    private PublishingStatus publishingStatusOf(final Boolean aPublished) {
        return aPublished == Boolean.TRUE ? PublishingStatus.PUBLISHED : PublishingStatus.NOT_PUBLISHED;
    }

    private ReleaseStatus releaseStatusOf(final Boolean anOpened) {
        return anOpened == Boolean.TRUE ? ReleaseStatus.RELEASED : ReleaseStatus.NOT_RELEASED;
    }

    private Resource resourceOf(final MultipartFile aMultipartFile) {
        if (aMultipartFile == null) {
            return null;
        }
        try {
            final var checksum = HashingUtils
                    .checksum(aMultipartFile.getBytes());
            return Resource.with(
                    checksum,
                    aMultipartFile.getBytes(),
                    aMultipartFile.getContentType(),
                    aMultipartFile.getOriginalFilename());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }
}
