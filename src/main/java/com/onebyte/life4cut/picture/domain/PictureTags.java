package com.onebyte.life4cut.picture.domain;

import jakarta.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PictureTags {

  private final List<PictureTag> tags;

  public PictureTags(@Nonnull List<PictureTag> tags) {
    this.tags = tags;
  }

  @Nonnull
  public PictureTags retainAll(@Nonnull String... tagNames) {
    return new PictureTags(
        tags.stream()
            .filter(tag -> Arrays.stream(tagNames).anyMatch(tag::isNameEqualsTo))
            .toList());
  }

  @Nonnull
  public PictureTags removeAll(@Nonnull String... tagNames) {
    return new PictureTags(
        tags.stream()
            .filter(tag -> Arrays.stream(tagNames).noneMatch(tag::isNameEqualsTo))
            .toList());
  }

  public boolean has(@Nonnull String tagName) {
    return tags.stream().anyMatch(tag -> tag.isNameEqualsTo(tagName));
  }

  @Nonnull
  public List<PictureTag> getTags() {
    return Collections.unmodifiableList(tags);
  }
}
