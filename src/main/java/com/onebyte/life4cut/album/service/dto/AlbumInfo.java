package com.onebyte.life4cut.album.service.dto;

import com.onebyte.life4cut.album.repository.dto.UserDetailResult;
import java.util.List;
import lombok.NonNull;

public record AlbumInfo(
    @NonNull Long albumId,
    @NonNull String albumName,
    @NonNull List<UserDetailResult> sharedUsers) {}
