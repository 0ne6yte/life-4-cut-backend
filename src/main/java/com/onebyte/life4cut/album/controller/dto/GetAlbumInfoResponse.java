package com.onebyte.life4cut.album.controller.dto;

import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.repository.dto.UserDetailResult;
import com.onebyte.life4cut.album.service.dto.AlbumInfo;
import jakarta.annotation.Nullable;
import java.util.List;
import lombok.NonNull;

public record GetAlbumInfoResponse(
    Long albumId, String albumName, Long sharedCount, List<SharedUser> sharedUsers) {

  public static GetAlbumInfoResponse of(AlbumInfo albumInfo) {
    return new GetAlbumInfoResponse(
        albumInfo.albumId(),
        albumInfo.albumName(),
        (long) albumInfo.sharedUsers().size(),
        albumInfo.sharedUsers().stream().map(SharedUser::of).toList());
  }

  record SharedUser(
      @NonNull Long id,
      @Nullable String profilePath,
      @NonNull String nickname,
      @NonNull UserAlbumRole role) {

    private static SharedUser of(UserDetailResult userDetailResult) {
      return new SharedUser(
          userDetailResult.userId(),
          userDetailResult.profilePath(),
          userDetailResult.nickname(),
          userDetailResult.role());
    }
  }
}
