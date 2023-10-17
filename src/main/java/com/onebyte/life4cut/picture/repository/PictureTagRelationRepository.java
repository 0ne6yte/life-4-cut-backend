package com.onebyte.life4cut.picture.repository;

import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import com.onebyte.life4cut.picture.domain.PictureTagRelations;
import java.util.List;

public interface PictureTagRelationRepository {

  PictureTagRelation save(PictureTagRelation pictureTagRelation);

  List<PictureTagRelation> saveAll(Iterable<PictureTagRelation> pictureTagRelations);

  PictureTagRelations findByPictureId(Long pictureId);
}
