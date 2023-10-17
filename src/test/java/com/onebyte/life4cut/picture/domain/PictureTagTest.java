package com.onebyte.life4cut.picture.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.onebyte.life4cut.fixture.PictureTagFixtureFactory;
import com.onebyte.life4cut.picture.domain.vo.PictureTagName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PictureTagTest {

  private final PictureTagFixtureFactory pictureTagFixtureFactory = new PictureTagFixtureFactory();

  @Nested
  class IsNameEqualsTo {
    @Test
    @DisplayName("태그 이름이 같으면 true를 반환한다")
    void is() {
      // given
      final String name = "태그 이름";
      PictureTag pictureTag =
          pictureTagFixtureFactory.make(
              (entity, builder) -> {
                builder.set("name", PictureTagName.of(name));
              });

      // when
      final boolean result = pictureTag.isNameEqualsTo(name);

      // then
      assertThat(result).isTrue();
    }

    @Test
    @DisplayName("태그 이름이 다르면 false를 반환한다")
    void is_not() {
      // given
      final String name = "태그 이름";
      PictureTag pictureTag =
          pictureTagFixtureFactory.make(
              (entity, builder) -> {
                builder.set("name", PictureTagName.of(name));
              });

      // when
      final boolean result = pictureTag.isNameEqualsTo("다른 태그 이름");

      // then
      assertThat(result).isFalse();
    }
  }
}
