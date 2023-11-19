package com.onebyte.life4cut.album.domain;

import com.onebyte.life4cut.common.entity.BaseEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;
import lombok.Setter;

@Entity
public class Album extends BaseEntity {

  @Setter
  @Nonnull
  @Column(nullable = false)
  private String name;

  @Nullable @Column private LocalDateTime deletedAt;

  public static Album create(@Nonnull String name) {
    Album album = new Album();
    album.name = name;
    return album;
  }

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }
}
