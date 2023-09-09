package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class VideoCastMemberID implements Serializable {

    @Serial
    private static final long serialVersionUID = 5427136797646949653L;

    @Column(name = "video_id")
    private String videoId;

    @Column(name = "cast_member_id")
    private String castMemberId;

    public VideoCastMemberID() {
    }

    private VideoCastMemberID(final String aVideoId, final String aCastMemberId) {
        this.videoId = aVideoId;
        this.castMemberId = aCastMemberId;
    }

    public static VideoCastMemberID from(final String aVideoId, final String aCastMemberId) {
        return new VideoCastMemberID(aVideoId, aCastMemberId);
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(final String videoId) {
        this.videoId = videoId;
    }

    public String getCastMemberId() {
        return castMemberId;
    }

    public void setCastMemberId(final String castMemberId) {
        this.castMemberId = castMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final VideoCastMemberID that = (VideoCastMemberID) o;
        return Objects.equals(getVideoId(), that.getVideoId()) && Objects.equals(getCastMemberId(), that.getCastMemberId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVideoId(), getCastMemberId());
    }
}
