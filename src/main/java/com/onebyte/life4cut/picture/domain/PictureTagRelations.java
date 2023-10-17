package com.onebyte.life4cut.picture.domain;

import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class PictureTagRelations {

  private final List<PictureTagRelation> relations;

  public PictureTagRelations(@Nonnull List<PictureTagRelation> relations) {
    this.relations = relations;
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
