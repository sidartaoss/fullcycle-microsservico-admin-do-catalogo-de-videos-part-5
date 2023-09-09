package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.resource.Resource;

import java.util.Optional;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(VideoID anId, VideoResource aVideoResource);

    ImageMedia storeImage(VideoID anId, VideoResource aVideoResource);

    Optional<Resource> getResource(VideoID anId, VideoMediaType aType);

    void clearResources(VideoID anId);
}
