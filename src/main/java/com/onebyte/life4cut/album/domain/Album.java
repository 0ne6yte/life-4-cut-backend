package com.onebyte.life4cut.album.domain;

import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Album extends BaseEntity {

  @Nonnull
  @Column(nullable = false)
  private String name;

  @Nullable @Column private LocalDateTime deletedAt;
}
