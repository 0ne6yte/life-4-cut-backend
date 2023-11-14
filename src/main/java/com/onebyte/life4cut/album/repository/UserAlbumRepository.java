package com.onebyte.life4cut.album.repository;

import com.onebyte.life4cut.album.domain.UserAlbum;
import java.util.Optional;

public interface UserAlbumRepository {
  Optional<UserAlbum> findByUserIdAndAlbumId(Long userId, Long albumId);

  UserAlbum save(UserAlbum userAlbum);

  void delete(UserAlbum userAlbum);

  void deleteByAlbumId(Long albumId);
}
