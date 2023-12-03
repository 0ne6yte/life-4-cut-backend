package com.onebyte.life4cut.album.repository;

import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.repository.dto.UserDetailResult;
import java.util.List;
import java.util.Optional;

public interface UserAlbumRepository {

  Optional<UserAlbum> findByUserIdAndAlbumId(Long userId, Long albumId);

  List<UserDetailResult> findUserDetailsByAlbumId(Long albumId);
}
