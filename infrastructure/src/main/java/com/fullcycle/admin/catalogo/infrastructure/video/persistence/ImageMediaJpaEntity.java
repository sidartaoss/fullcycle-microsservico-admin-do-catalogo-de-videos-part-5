package com.fullcycle.admin.catalogo.infrastructure.video.persistence;

import com.fullcycle.admin.catalogo.domain.video.ImageMedia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity(name = "ImageMedia")
@Table(name = "videos_image_media")
public class ImageMediaJpaEntity {

    @Id
    private String id;

    private String checksum;

    private String name;

    @Column(name = "file_path")
    private String filePath;

    public ImageMediaJpaEntity() {
    }

    private ImageMediaJpaEntity(
            final String anId,
            final String aChecksum,
            final String aName,
            final String aFilePath) {
        this.id = anId;
        this.checksum = aChecksum;
        this.name = aName;
        this.filePath = aFilePath;
    }

    public static ImageMediaJpaEntity from(final ImageMedia anImageMedia) {
        return new ImageMediaJpaEntity(
                anImageMedia.id(),
                anImageMedia.checksum(),
                anImageMedia.name(),
                anImageMedia.location()
        );
    }

    public ImageMedia toDomain() {
        return ImageMedia.with(
                getId(),
                getChecksum(),
                getName(),
                getFilePath()
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
