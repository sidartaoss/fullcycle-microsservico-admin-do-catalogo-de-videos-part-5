package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VideoGenreID implements Serializable {

    @Serial
    private static final long serialVersionUID = 7532162408324596624L;

    @Column(name = "video_id")
    private String videoId;

    @Column(name = "genre_id")
    private String genreId;

    public VideoGenreID() {
    }

    private VideoGenreID(final String aVideoId, final String aGenreId) {
        this.videoId = aVideoId;
        this.genreId = aGenreId;
    }

    public static VideoGenreID from(final String aVideoId, final String aGenreId) {
        return new VideoGenreID(aVideoId, aGenreId);
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(final String videoId) {
        this.videoId = videoId;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(final String genreId) {
        this.genreId = genreId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoGenreID that = (VideoGenreID) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getGenreId(), that.getGenreId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getGenreId());
    }
}
