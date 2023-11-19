package com.onebyte.life4cut.album.repository;

import static com.onebyte.life4cut.album.domain.QUserAlbum.userAlbum;

import com.onebyte.life4cut.album.domain.UserAlbum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserAlbumRepositoryImpl implements UserAlbumRepository {

  private final JPAQueryFactory jpaQueryFactory;
  @PersistenceContext private EntityManager entityManager;

  public UserAlbumRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
    this.jpaQueryFactory = jpaQueryFactory;
  }

  public Optional<UserAlbum> findByUserIdAndAlbumId(Long userId, Long albumId) {
    return Optional.ofNullable(
        jpaQueryFactory
            .selectFrom(userAlbum)
            .where(
                userAlbum.userId.eq(userId),
                userAlbum.albumId.eq(albumId),
                userAlbum.deletedAt.isNull())
            .fetchOne());
  }

  @Override
  public UserAlbum save(UserAlbum userAlbum) {
    entityManager.persist(userAlbum);
    return userAlbum;
  }

  @Transactional
  @Override
  public void delete(UserAlbum userAlbum) {
    userAlbum.softDelete();
    entityManager.merge(userAlbum);
  }

  @Transactional
  @Override
  public void deleteByAlbumId(Long albumId) {
    jpaQueryFactory
        .update(userAlbum)
        .set(userAlbum.deletedAt, LocalDateTime.now())
        .where(userAlbum.albumId.eq(albumId))
        .execute();
  }
}
