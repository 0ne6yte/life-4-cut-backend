package com.onebyte.life4cut.pictureTag.domain.vo;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@EqualsAndHashCode
public class PictureTagName {

  private static final int MAX_LENGTH = 30;

  @Nonnull
  @Column(nullable = false, length = 30, name = "name")
  private String value;

  private PictureTagName(final @Nonnull String value) {
    this.value = value;
  }

  @Nonnull
  public static PictureTagName of(@Nonnull final String value) {
    if (value.trim().length() > MAX_LENGTH) {
      throw new IllegalArgumentException("태그는 30자를 초과할 수 없습니다");
    }

    return new PictureTagName(value.trim());
  }

  public boolean is(String name) {
    return this.value.equals(name);
  }
}
