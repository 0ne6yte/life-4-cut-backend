package com.onebyte.life4cut.album.service;

import com.onebyte.life4cut.album.domain.Album;
import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.exception.AlbumNotFoundException;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.AlbumRepository;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import jakarta.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

  public Long createAlbum(
      @Nonnull String name,
      @Nonnull Long userId,
      List<Long> memberUserIds,
      List<Long> guestUserIds) {
    Set<Long> userIds = new HashSet<>();

    Album album = Album.create(name);
    albumRepository.save(album);
    Long albumId = album.getId();
    UserAlbum hostUserAlbum = UserAlbum.createHost(albumId, userId);

    userAlbumRepository.save(hostUserAlbum);
    userIds.add(userId);

    if (memberUserIds != null) {
      for (Long memberId : memberUserIds) {
        if (userIds.add(memberId)) {
          UserAlbum memberUserAlbum = UserAlbum.createMember(albumId, memberId);
          userAlbumRepository.save(memberUserAlbum);
        }
      }
    }
    if (guestUserIds != null) {
      for (Long guestId : guestUserIds) {
        if (userIds.add(guestId)) {
          UserAlbum guestUserAlbum = UserAlbum.createGuest(albumId, guestId);
          userAlbumRepository.save(guestUserAlbum);
        }
      }
    }

    return albumId;
  }

  public void deleteAlbum(Long albumId, @NonNull Long userId) {
    albumRepository.findById(albumId).orElseThrow(AlbumNotFoundException::new);

    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(userId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);

    if (userAlbum.isHost()) {
      albumRepository.deleteById(albumId);
      userAlbumRepository.deleteByAlbumId(albumId);
    } else {
      userAlbumRepository.delete(userAlbum);
    }
  }

  public void updateAlbum(
      Long albumId, String name, Long userId, List<Long> memberUserIds, List<Long> guestUserIds) {
    Album album = albumRepository.findById(albumId).orElseThrow(() -> new AlbumNotFoundException());
    UserAlbum userAlbum =
        userAlbumRepository
            .findByUserIdAndAlbumId(userId, albumId)
            .orElseThrow(UserAlbumRolePermissionException::new);

    if (!userAlbum.isHost()) {
      throw new UserAlbumRolePermissionException();
    }
    userAlbumRepository.deleteByAlbumId(albumId);
    if (name != null) {
      album.setName(name);
    }
    albumRepository.save(album);

    Set<Long> userIds = new HashSet<>();

    UserAlbum hostUserAlbum = UserAlbum.createHost(albumId, userId);

    userAlbumRepository.save(hostUserAlbum);
    userIds.add(userId);

    if (memberUserIds != null) {
      for (Long memberId : memberUserIds) {
        if (userIds.add(memberId)) {
          UserAlbum memberUserAlbum = UserAlbum.createMember(albumId, memberId);
          userAlbumRepository.save(memberUserAlbum);
        }
      }
    }
    if (guestUserIds != null) {
      for (Long guestId : guestUserIds) {
        if (userIds.add(guestId)) {
          UserAlbum guestUserAlbum = UserAlbum.createGuest(albumId, guestId);
          userAlbumRepository.save(guestUserAlbum);
        }
      }
    }
  }
}
