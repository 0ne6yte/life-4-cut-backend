package com.onebyte.life4cut.picture.domain;

import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class PictureTags {

  private final List<PictureTag> tags;

  public PictureTags(@Nonnull List<PictureTag> tags) {
    this.tags = tags;
  }

  @Nonnull
  public PictureTags retainAll(@Nonnull List<String> tagNames) {
    return new PictureTags(
        tags.stream().filter(tag -> tagNames.stream().anyMatch(tag::isNameEqualsTo)).toList());
  }

  @Nonnull
  public PictureTags removeAll(@Nonnull List<String> tagNames) {
    return new PictureTags(
        tags.stream().filter(tag -> tagNames.stream().noneMatch(tag::isNameEqualsTo)).toList());
  }

  public boolean has(@Nonnull String tagName) {
    return tags.stream().anyMatch(tag -> tag.isNameEqualsTo(tagName));
  }

  @Nonnull
  public List<PictureTag> getTags() {
    return Collections.unmodifiableList(tags);
  }
}
