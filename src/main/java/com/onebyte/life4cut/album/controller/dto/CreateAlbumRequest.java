package com.onebyte.life4cut.album.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateAlbumRequest(
    @NotNull(message = "앨범의 이름을 입력해주세요") String name,
    List<@Min(value = 1, message = "올바른 memberUserIds를 입력해주세요") Long> memberUserIds,
    List<@Min(value = 1, message = "올바른 guestUserIds를 입력해주세요") Long> guestUserIds) {}
