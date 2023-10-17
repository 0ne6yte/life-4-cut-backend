package com.onebyte.life4cut.picture.repository;

import com.onebyte.life4cut.picture.domain.PictureTagRelation;
import com.onebyte.life4cut.picture.domain.PictureTagRelations;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PictureTagRelationRepositoryImpl implements PictureTagRelationRepository {

  private final EntityManager em;

  @Override
  public PictureTagRelation save(PictureTagRelation pictureTagRelation) {
    em.persist(pictureTagRelation);
    return pictureTagRelation;
  }

  @Override
  public List<PictureTagRelation> saveAll(Iterable<PictureTagRelation> pictureTagRelations) {
    List<PictureTagRelation> results = new ArrayList<>();

    for (PictureTagRelation pictureTagRelation : pictureTagRelations) {
      results.add(save(pictureTagRelation));
    }

    return results;
  }

  @Override
  public PictureTagRelations findByPictureId(Long pictureId) {
    return new PictureTagRelations(
        em.createQuery(
                "SELECT ptr FROM PictureTagRelation ptr WHERE ptr.pictureId = :pictureId",
                PictureTagRelation.class)
            .setParameter("pictureId", pictureId)
            .getResultList());
  }
}
