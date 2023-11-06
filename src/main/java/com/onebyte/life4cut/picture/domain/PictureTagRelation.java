package com.onebyte.life4cut.picture.domain;

import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "picture_tag_relation",
    indexes = {
      @Index(name = "idx_picture_tag_relation_1", columnList = "picture_id,tag_id", unique = true),
      @Index(name = "idx_picture_tag_relation_2", columnList = "album_id"),
      @Index(name = "idx_picture_tag_relation_3", columnList = "tag_id")
    })
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class PictureTagRelation extends BaseEntity {

  @Nonnull
  @ManyToOne
  @JoinColumn(name = "picture_id", nullable = false)
  private Picture picture;

  @Nonnull
  @Column(nullable = false, name = "album_id")
  private Long albumId;

  @Nonnull
  @Column(nullable = false, name = "tag_id")
  private Long tagId;

  @Nonnull
  public static PictureTagRelation create(@Nonnull Picture picture, @Nonnull Long tagId) {
    PictureTagRelation pictureTagRelation = new PictureTagRelation();
    pictureTagRelation.picture = picture;
    pictureTagRelation.albumId = picture.getAlbumId();
    pictureTagRelation.tagId = tagId;
    return pictureTagRelation;
  }
}
