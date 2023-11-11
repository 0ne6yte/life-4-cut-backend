package com.onebyte.life4cut.common.vo;

import jakarta.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ImagePath {

  private String value;

  protected ImagePath(@Nonnull String value) {
    this.value = value;
  }

  @Nonnull
  public static ImagePath of(@Nonnull String value) {
    return new ImagePath(value);
  }

  @Nonnull
  public String getValue() {
    return value;
  }
}
