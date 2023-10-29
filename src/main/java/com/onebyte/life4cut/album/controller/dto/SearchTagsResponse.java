package com.onebyte.life4cut.album.controller.dto;

import com.onebyte.life4cut.pictureTag.domain.PictureTag;
import java.util.List;

public record SearchTagsResponse(List<TagResponse> tags) {

  public static SearchTagsResponse of(List<PictureTag> pictureTags) {
    return new SearchTagsResponse(
        pictureTags.stream()
            .map(pictureTag -> new TagResponse(pictureTag.getId(), pictureTag.getName().getValue()))
            .toList());
  }

  record TagResponse(Long id, String name) {}
}
