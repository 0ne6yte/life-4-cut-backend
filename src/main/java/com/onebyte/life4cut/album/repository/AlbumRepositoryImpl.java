package com.onebyte.life4cut.album.repository;

import static com.onebyte.life4cut.album.domain.QAlbum.album;

import com.onebyte.life4cut.album.domain.Album;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class AlbumRepositoryImpl implements AlbumRepository {

  private final JPAQueryFactory jpaQueryFactory;
  @PersistenceContext private EntityManager entityManager;

  public AlbumRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  public Optional<Album> findById(Long id) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(album)
            .where(album.id.eq(id), album.deletedAt.isNull())
            .fetchOne());
  }

  @Transactional
  public Album save(Album album) {
    entityManager.persist(album);
    return album;
  }

  @Override
  public void deleteById(Long id) {
    Album album = entityManager.find(Album.class, id);

    if (album != null) {
      album.softDelete();
      entityManager.merge(album);
    }
  }
}
