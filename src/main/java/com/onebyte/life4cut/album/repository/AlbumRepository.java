package com.onebyte.life4cut.album.repository;

import com.onebyte.life4cut.album.domain.Album;
import java.util.Optional;

public interface AlbumRepository {
  Optional<Album> findById(Long id);

  Album save(Album album);
}
