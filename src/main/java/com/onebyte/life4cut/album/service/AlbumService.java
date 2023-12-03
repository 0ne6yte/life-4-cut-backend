package com.onebyte.life4cut.album.service;

import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.exception.AlbumNotFoundException;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.AlbumRepository;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import com.onebyte.life4cut.album.repository.dto.UserDetailResult;
import com.onebyte.life4cut.album.service.dto.AlbumInfo;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlbumService {

  private final UserAlbumRepository userAlbumRepository;
  private final AlbumRepository albumRepository;

  public UserAlbumRole getRoleInAlbum(@NonNull Long albumId, @NonNull Long userId) {
    albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(userId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);

    return userAlbum.getRole();
  }

  public AlbumInfo getAlbumInfo(@NonNull Long albumId, @NonNull Long userId) {
    Album album = albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);
    List<UserDetailResult> userDetailsResults =
        userAlbumRepository.findUserDetailsByAlbumId(albumId);

    List<Long> albumUserIds = userDetailsResults.stream().map(UserDetailResult::userId).toList();

    if (!albumUserIds.contains(userId)) {
      throw new UserAlbumRolePermissionException();
    }

    return new AlbumInfo(album.getId(), album.getName(), userDetailsResults);
  }
}
