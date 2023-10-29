package com.onebyte.life4cut.pictureTag.repository;

import com.onebyte.life4cut.pictureTag.domain.PictureTag;
import com.onebyte.life4cut.pictureTag.domain.PictureTags;
import java.util.List;

public interface PictureTagRepository {

  PictureTags findByNames(Long albumId, List<String> names);

  List<PictureTag> saveAll(Iterable<PictureTag> pictureTags);

  PictureTag save(PictureTag pictureTag);

  List<PictureTag> search(Long albumId, String keyword);
}
