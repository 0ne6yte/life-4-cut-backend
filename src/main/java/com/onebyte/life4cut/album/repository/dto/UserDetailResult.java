package com.onebyte.life4cut.album.repository.dto;

import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import io.micrometer.common.lang.Nullable;
import lombok.NonNull;

public record UserDetailResult(
    @NonNull Long userId,
    @Nullable String profilePath,
    @NonNull String nickname,
    @NonNull UserAlbumRole role) {}
