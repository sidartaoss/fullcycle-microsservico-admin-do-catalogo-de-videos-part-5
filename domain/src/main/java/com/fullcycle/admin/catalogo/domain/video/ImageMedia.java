package com.fullcycle.admin.catalogo.domain.video;

import com.fullcycle.admin.catalogo.domain.ValueObject;
import com.fullcycle.admin.catalogo.domain.utils.IdUtils;

import java.util.Objects;

public class ImageMedia extends ValueObject {

    private final String id;
    private final String checksum;
    private final String name;
    private final String location;

    private ImageMedia(
            final String id,
            final String checksum,
            final String name,
            final String location) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("'id' should not be empty or null");
        }
        if (checksum == null || checksum.isBlank()) {
            throw new IllegalArgumentException("'checksum' should not be empty or null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("'name' should not be empty or null");
        }
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("'location' should not be empty or null");
        }
        this.id = id;
        this.checksum = checksum;
        this.name = name;
        this.location = location;
    }

    public static ImageMedia with(
            final String id,
            final String aChecksum,
            final String aName,
            final String aLocation
    ) {
        return new ImageMedia(id, aChecksum, aName, aLocation);
    }

    public static ImageMedia with(
            final String aChecksum,
            final String aName,
            final String aLocation
    ) {
        return with(IdUtils.uuid(), aChecksum, aName, aLocation);
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

    public String location() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageMedia that = (ImageMedia) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
