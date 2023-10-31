package com.onebyte.life4cut.pictureTag.domain.vo;

import com.onebyte.life4cut.pictureTag.domain.PictureTag;
import jakarta.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class PictureTags {

  private final List<PictureTag> tags;

  public PictureTags(@Nonnull List<PictureTag> tags) {
    this.tags = tags;
  }

  @Nonnull
  public PictureTags retainAll(@Nonnull List<String> tagNames) {
    if (tagNames.isEmpty()) {
      return new PictureTags(Collections.emptyList());
    }

    return new PictureTags(
        tags.stream().filter(tag -> tagNames.stream().anyMatch(tag::isNameEqualsTo)).toList());
  }

  @Nonnull
  public PictureTags removeAll(@Nonnull List<String> tagNames) {
    if (tagNames.isEmpty()) {
      return this;
    }

    return new PictureTags(
        tags.stream().filter(tag -> tagNames.stream().noneMatch(tag::isNameEqualsTo)).toList());
  }

  public boolean has(@Nonnull String tagName) {
    return tags.stream().anyMatch(tag -> tag.isNameEqualsTo(tagName));
  }

  @Nonnull
  public PictureTags addAll(@Nonnull List<PictureTag> pictureTags) {
    if (pictureTags.isEmpty()) {
      return this;
    }

    return new PictureTags(Stream.concat(tags.stream(), pictureTags.stream()).toList());
  }

  @Nonnull
  public List<PictureTag> getTags() {
    return Collections.unmodifiableList(tags);
  }
}
