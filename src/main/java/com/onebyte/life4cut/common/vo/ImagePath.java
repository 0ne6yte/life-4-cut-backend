package com.onebyte.life4cut.common.vo;

import jakarta.annotation.Nonnull;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ImagePath {

  private String value;

  private ImagePath() {}

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
