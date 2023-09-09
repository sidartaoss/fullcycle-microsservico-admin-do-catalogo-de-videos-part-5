package com.fullcycle.admin.catalogo.domain.resource;

import com.fullcycle.admin.catalogo.domain.ValueObject;

import java.util.Arrays;
import java.util.Objects;

public class Resource extends ValueObject {

    private final String checksum;
    private final byte[] content;
    private final String contentType;
    private final String name;

    private Resource(
            final String checksum,
            final byte[] aContent,
            final String aContentType,
            final String aName) {
        if (checksum == null || checksum.isBlank()) {
            throw new IllegalArgumentException("'checksum' should not be null or empty");
        }
        if (aContentType == null || aContentType.isBlank()) {
            throw new IllegalArgumentException("'contentType' should not be null or empty");
        }
        if (aName == null || aName.isBlank()) {
            throw new IllegalArgumentException("'name' should not be null or empty");
        }
        this.checksum = checksum;
        this.content = Objects.requireNonNull(aContent);
        this.contentType = aContentType;
        this.name = aName;
    }

    public static Resource with(
            final String aChecksum,
            final byte[] aContent,
            final String aContentType,
            final String aName
    ) {
        return new Resource(aChecksum, aContent, aContentType, aName);
    }

    public String checksum() {
        return checksum;
    }

    public byte[] content() {
        return content;
    }

    public String contentType() {
        return contentType;
    }

    public String name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Resource resource = (Resource) o;
        return Objects.equals(checksum, resource.checksum)
                && Arrays.equals(content, resource.content)
                && Objects.equals(contentType, resource.contentType)
                && Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(checksum, contentType, name);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }
}
