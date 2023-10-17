package com.onebyte.life4cut.picture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.onebyte.life4cut.fixture.PictureTagRelationFixtureFactory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PictureTagRelationsTest {

  PictureTagRelationFixtureFactory pictureTagRelationFixtureFactory =
      new PictureTagRelationFixtureFactory();

  @Nested
  class RetainAll {
    @Test
    @DisplayName("태그 아이디가 같은 태그만 남는다")
    void retainAll() {
      // given
      List<Long> tagIds = List.of(1L, 2L, 3L);
      List<PictureTagRelation> relations =
          tagIds.stream()
              .map(
                  tagId ->
                      pictureTagRelationFixtureFactory.make(
                          (relation, builder) -> builder.set("tagId", tagId)))
              .toList();

      PictureTagRelations pictureTagRelations = new PictureTagRelations(relations);

      // when
      PictureTagRelations retainedRelations = pictureTagRelations.retainAll(List.of(2L, 3L));

      // then
      assertThat(retainedRelations.getRelations()).hasSize(2);
      assertThat(retainedRelations.getRelations().get(0).getTagId()).isEqualTo(2L);
      assertThat(retainedRelations.getRelations().get(1).getTagId()).isEqualTo(3L);
    }
  }

  @Nested
  class RemoveAll {
    @Test
    @DisplayName("태그 아이디가 같은 태그만 제거한다")
    void removeAll() {
      // given
      List<Long> tagIds = List.of(1L, 2L, 3L);
      List<PictureTagRelation> relations =
          tagIds.stream()
              .map(
                  tagId ->
                      pictureTagRelationFixtureFactory.make(
                          (relation, builder) -> builder.set("tagId", tagId)))
              .toList();

      PictureTagRelations pictureTagRelations = new PictureTagRelations(relations);

      // when
      PictureTagRelations retainedRelations = pictureTagRelations.removeAll(List.of(2L, 3L));

      // then
      assertThat(retainedRelations.getRelations()).hasSize(1);
      assertThat(retainedRelations.getRelations().get(0).getTagId()).isEqualTo(1L);
    }
  }

  @Nested
  class Has {
    @Test
    @DisplayName("태그 아이디가 같은 태그가 있는지 확인한다")
    void has() {
      // given
      List<Long> tagIds = List.of(1L, 2L, 3L);
      List<PictureTagRelation> relations =
          tagIds.stream()
              .map(
                  tagId ->
                      pictureTagRelationFixtureFactory.make(
                          (relation, builder) -> builder.set("tagId", tagId)))
              .toList();

      PictureTagRelations pictureTagRelations = new PictureTagRelations(relations);

      // when
      boolean has = pictureTagRelations.has(2L);

      // then
      assertThat(has).isTrue();
    }

    @Test
    @DisplayName("태그 아이디가 같은 태그가 없는지 확인한다")
    void hasNot() {
      // given
      List<Long> tagIds = List.of(1L, 2L, 3L);
      List<PictureTagRelation> relations =
          tagIds.stream()
              .map(
                  tagId ->
                      pictureTagRelationFixtureFactory.make(
                          (relation, builder) -> builder.set("tagId", tagId)))
              .toList();

      PictureTagRelations pictureTagRelations = new PictureTagRelations(relations);

      // when
      boolean has = pictureTagRelations.has(4L);

      // then
      assertThat(has).isFalse();
    }
  }
}
