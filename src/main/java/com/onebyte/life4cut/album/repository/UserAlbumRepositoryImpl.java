package com.onebyte.life4cut.album.repository;

import static com.onebyte.life4cut.album.domain.QUserAlbum.userAlbum;
import static com.onebyte.life4cut.user.domain.QUser.user;

import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.repository.dto.UserDetailResult;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserAlbumRepositoryImpl implements UserAlbumRepository {

  private final JPAQueryFactory jpaQueryFactory;

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

  public List<UserDetailResult> findUserDetailsByAlbumId(Long albumId) {
    return jpaQueryFactory
        .select(
            Projections.constructor(
                UserDetailResult.class, user.id, user.profilePath, user.nickname, userAlbum.role))
        .from(userAlbum)
        .leftJoin(user)
        .on(userAlbum.userId.eq(user.id))
        .where(userAlbum.albumId.eq(albumId))
        .fetch();
  }
}
