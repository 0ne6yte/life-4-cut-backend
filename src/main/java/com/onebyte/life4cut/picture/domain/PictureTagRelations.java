package com.onebyte.life4cut.picture.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class PictureTagRelations {

  @OneToMany(mappedBy = "picture", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PictureTagRelation> relations = new ArrayList<>();

  public PictureTagRelations(@Nonnull List<PictureTagRelation> relations) {
    this.relations = relations;
  }

  public static PictureTagRelations of(Picture picture, PictureTags pictureTags) {
    return new PictureTagRelations(
        pictureTags.getTags().stream()
            .map(pictureTag -> PictureTagRelation.create(picture, pictureTag.getId()))
            .toList());
  }

  @Nonnull
  public PictureTagRelations retainAll(@Nonnull List<Long> tagIds) {
    if (tagIds.isEmpty()) {
      return this;
    }

    return new PictureTagRelations(
        relations.stream()
            .filter(relation -> tagIds.stream().anyMatch(id -> relation.getTagId().equals(id)))
            .toList());
  }

  @Nonnull
  public PictureTagRelations removeAll(@Nonnull List<Long> tagIds) {
    if (tagIds.isEmpty()) {
      return this;
    }

    return new PictureTagRelations(
        relations.stream()
            .filter(relation -> tagIds.stream().noneMatch(id -> relation.getTagId().equals(id)))
            .toList());
  }

  public boolean has(@Nonnull Long id) {
    return relations.stream().anyMatch(relation -> relation.getTagId().equals(id));
  }

  @Nonnull
  public List<PictureTagRelation> getRelations() {
    return Collections.unmodifiableList(relations);
  }
}
