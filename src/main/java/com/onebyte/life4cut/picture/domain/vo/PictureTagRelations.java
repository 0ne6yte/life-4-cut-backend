package com.onebyte.life4cut.picture.domain.vo;

import com.onebyte.life4cut.picture.domain.Picture;
import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import com.onebyte.life4cut.pictureTag.domain.vo.PictureTags;
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

  /**
   * 태그 변경시 일반적인 vo 사용방법대로 새로운 PictureTagRelations를 생성하도록 하려고 했었음. 그러나 persist시, Hibernate는 컬렉션 추적
   * 관리를 위해 PersistentBag이라는 wrapper 클래스로 감싸고, 참조를 변경하게 되면 예외를 발생시킴. 그래서 collection을 변경하는 방식으로 구현 및
   * 외부에서는 이를 모르게 새로운 vo가 만들어진 것 처럼 사용하도록 함.
   */
  public PictureTagRelations update(Picture picture, PictureTags pictureTags) {
    relations.removeAll(
        relations.stream()
            .filter(
                relation ->
                    pictureTags.getTags().stream()
                        .noneMatch(pictureTag -> relation.getTagId().equals(pictureTag.getId())))
            .toList());

    pictureTags.getTags().stream()
        .filter(
            pictureTag ->
                relations.stream()
                    .noneMatch(relation -> relation.getTagId().equals(pictureTag.getId())))
        .forEach(
            pictureTag -> relations.add(PictureTagRelation.create(picture, pictureTag.getId())));

    return this;
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
