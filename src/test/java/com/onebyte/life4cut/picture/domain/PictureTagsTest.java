package com.onebyte.life4cut.picture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PictureTagsTest {

  PictureTagFixtureFactory pictureTagFixtureFactory = new PictureTagFixtureFactory();

  @Nested
  class RetainAll {

    @Test
    @DisplayName("태그 이름이 같은 태그만 남는다")
    void retainAll() {
      // given
      List<String> tagNames = List.of("태그1", "태그2", "태그3");
      List<PictureTag> tags =
          tagNames.stream()
              .map(
                  name ->
                      pictureTagFixtureFactory.make(
                          (tag, builder) -> builder.set("name", PictureTagName.of(name))))
              .toList();

      PictureTags pictureTags = new PictureTags(tags);

      // when
      PictureTags retainedTags = pictureTags.retainAll("태그2", "태그3");

      // then
      assertThat(retainedTags.getTags()).hasSize(2);
      assertThat(retainedTags.getTags().get(0).getName().getValue()).isEqualTo("태그2");
      assertThat(retainedTags.getTags().get(1).getName().getValue()).isEqualTo("태그3");
    }
  }

  @Nested
  class RemoveAll {

    @Test
    @DisplayName("태그 이름이 같은 태그만 제거한다")
    void removeAll() {
      // given
      List<String> tagNames = List.of("태그1", "태그2", "태그3");
      List<PictureTag> tags =
          tagNames.stream()
              .map(
                  name ->
                      pictureTagFixtureFactory.make(
                          (tag, builder) -> builder.set("name", PictureTagName.of(name))))
              .toList();

      PictureTags pictureTags = new PictureTags(tags);

      // when
      PictureTags retainedTags = pictureTags.removeAll("태그2", "태그3");

      // then
      assertThat(retainedTags.getTags()).hasSize(1);
      assertThat(retainedTags.getTags().get(0).getName().getValue()).isEqualTo("태그1");
    }
  }

  @Nested
  class Has {

    @Test
    @DisplayName("태그 이름이 같은 태그가 있는지 확인한다")
    void has() {
      // given
      List<String> tagNames = List.of("태그1", "태그2", "태그3");
      List<PictureTag> tags =
          tagNames.stream()
              .map(
                  name ->
                      pictureTagFixtureFactory.make(
                          (tag, builder) -> builder.set("name", PictureTagName.of(name))))
              .toList();

      PictureTags pictureTags = new PictureTags(tags);

      // when
      boolean hasTag = pictureTags.has("태그2");

      // then
      assertThat(hasTag).isTrue();
    }

    @Test
    @DisplayName("태그 이름이 같은 태그가 없는지 확인한다")
    void hasNot() {
      // given
      List<String> tagNames = List.of("태그1", "태그2", "태그3");
      List<PictureTag> tags =
          tagNames.stream()
              .map(
                  name ->
                      pictureTagFixtureFactory.make(
                          (tag, builder) -> builder.set("name", PictureTagName.of(name))))
              .toList();

      PictureTags pictureTags = new PictureTags(tags);

      // when
      boolean hasTag = pictureTags.has("태그4");

      // then
      assertThat(hasTag).isFalse();
    }
  }
}
