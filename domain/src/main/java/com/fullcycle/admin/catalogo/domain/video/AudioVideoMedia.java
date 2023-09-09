package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.ValueObject;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;

import java.util.Objects;

public class AudioVideoMedia extends ValueObject {

    private final String id;
    private final String checksum;
    private final String name;
    private final String rawLocation;
    private String encodedLocation;
    private final MediaStatus status;

    private AudioVideoMedia(
            final String id,
            final String checksum,
            final String name,
            final String rawLocation,
            final String encodedLocation,
            final MediaStatus status) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("'id' should not be empty or null");
        }
        if (checksum == null || checksum.isBlank()) {
            throw new IllegalArgumentException("'checksum' should not be empty or null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("'name' should not be empty or null");
        }
        if (rawLocation == null || rawLocation.isBlank()) {
            throw new IllegalArgumentException("'rawLocation' should not be empty or null");
        }
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.rawLocation = rawLocation;
        this.encodedLocation = Objects.requireNonNull(encodedLocation);
        this.status = Objects.requireNonNull(status);
    }

    public static AudioVideoMedia with(
            final String anId,
            final String aChecksum,
            final String aName,
            final String aRawLocation,
            final String anEncodedLocation,
            final MediaStatus aStatus
    ) {
        return new AudioVideoMedia(anId, aChecksum, aName, aRawLocation, anEncodedLocation, aStatus);
    }

    public static AudioVideoMedia with(
            final String aChecksum,
            final String aName,
            final String aRawLocation
    ) {
        return new AudioVideoMedia(IdUtils.uuid(), aChecksum, aName, aRawLocation, "",
                MediaStatus.PENDING);
    }

    public String id() {
        return id;
    }

    public String checksum() {
        return checksum;
    }

    public String name() {
        return name;
    }

    public String rawLocation() {
        return rawLocation;
    }

    public String encodedLocation() {
        return encodedLocation;
    }

    public MediaStatus status() {
        return status;
    }

    public AudioVideoMedia processing() {
        return AudioVideoMedia.with(
                id(),
                checksum(),
                name(),
                rawLocation(),
                encodedLocation(),
                MediaStatus.PROCESSING
        );
    }

    public AudioVideoMedia completed(final String encodedPath) {
        return AudioVideoMedia.with(
                id(),
                checksum(),
                name(),
                rawLocation(),
                encodedPath,
                MediaStatus.COMPLETED
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AudioVideoMedia that = (AudioVideoMedia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isPendingEncode() {
        return MediaStatus.PENDING == this.status;
    }
}
