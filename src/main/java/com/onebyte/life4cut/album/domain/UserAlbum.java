package com.onebyte.life4cut.album.domain;

import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class UserAlbum extends BaseEntity {

  @Nonnull
  @Column(nullable = false)
  private Long userId;

  @Nonnull
  @Column(nullable = false)
  private Long albumId;

  @Nonnull
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserAlbumRole role;

  @Nullable @Column private LocalDateTime deletedAt;

  public boolean isGuest() {
    return role == UserAlbumRole.GUEST;
  }

  public static UserAlbum createHost(@Nonnull Long albumId, @Nonnull Long userId) {
    return createUserAlbum(albumId, userId, UserAlbumRole.HOST);
  }

  public static UserAlbum createMember(@Nonnull Long albumId, @Nonnull Long userId) {
    return createUserAlbum(albumId, userId, UserAlbumRole.MEMBER);
  }

  public static UserAlbum createGuest(@Nonnull Long albumId, @Nonnull Long userId) {
    return createUserAlbum(albumId, userId, UserAlbumRole.GUEST);
  }

  private static UserAlbum createUserAlbum(
      @Nonnull Long albumId, @Nonnull Long userId, @Nonnull UserAlbumRole role) {
    UserAlbum userAlbum = new UserAlbum();
    userAlbum.albumId = albumId;
    userAlbum.userId = userId;
    userAlbum.role = role;
    return userAlbum;
  }
}
