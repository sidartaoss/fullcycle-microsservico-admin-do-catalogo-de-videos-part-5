package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VideoCategoryID implements Serializable {

    @Serial
    private static final long serialVersionUID = -8965048561640206822L;

    @Column(name = "video_id")
    private String videoId;

    @Column(name = "category_id")
    private String categoryId;

    public VideoCategoryID() {
    }

    private VideoCategoryID(final String aVideoId, final String aCategoryId) {
        this.videoId = aVideoId;
        this.categoryId = aCategoryId;
    }

    public static VideoCategoryID from(final String aVideoId, final String aCategoryId) {
        return new VideoCategoryID(aVideoId, aCategoryId);
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(final String videoId) {
        this.videoId = videoId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(final String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoCategoryID that = (VideoCategoryID) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getCategoryId(), that.getCategoryId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getCategoryId());
    }
}
