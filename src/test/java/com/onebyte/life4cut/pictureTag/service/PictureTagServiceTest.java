package com.onebyte.life4cut.pictureTag.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.onebyte.life4cut.album.domain.UserAlbum;
import com.onebyte.life4cut.album.domain.vo.UserAlbumRole;
import com.onebyte.life4cut.album.exception.UserAlbumRolePermissionException;
import com.onebyte.life4cut.album.repository.UserAlbumRepository;
import com.onebyte.life4cut.fixture.UserAlbumFixtureFactory;
import com.onebyte.life4cut.pictureTag.domain.PictureTag;
import com.onebyte.life4cut.pictureTag.repository.PictureTagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PictureTagServiceTest {

  private final PictureTagRepository pictureTagRepository = mock(PictureTagRepository.class);
  private final UserAlbumRepository userAlbumRepository = mock(UserAlbumRepository.class);
  private final UserAlbumFixtureFactory userAlbumFixtureFactory = new UserAlbumFixtureFactory();
  private final PictureTagService pictureTagService =
      new PictureTagService(pictureTagRepository, userAlbumRepository);

  @Nested
  class SearchTags {

    @Test
    @DisplayName("앨범 권한이 없는 경우 UserAlbumRolePermissionException 예외가 발생한다")
    void noUserAlbumRole() {
      // given
      String keyword = "bell";
      Long albumId = 1L;
      Long userId = 1L;

      when(userAlbumRepository.findByUserIdAndAlbumId(userId, albumId))
          .thenReturn(Optional.empty());

      // when
      Exception exception =
          catchException(() -> pictureTagService.searchTags(albumId, userId, keyword));

      // then
      assertThat(exception).isInstanceOf(UserAlbumRolePermissionException.class);
    }

    @Test
    @DisplayName("권한이 있는 경우 태그를 조회한다")
    void searchTags() {
      // given
      String keyword = "bell";
      Long albumId = 1L;
      Long userId = 1L;

      UserAlbum host =
          userAlbumFixtureFactory.make(
              (entity, builder) -> {
                builder.set("userId", userId);
                builder.set("albumId", albumId);
                builder.set("role", UserAlbumRole.HOST);
              });

      when(userAlbumRepository.findByUserIdAndAlbumId(userId, albumId))
          .thenReturn(Optional.of(host));

      when(pictureTagRepository.search(albumId, keyword)).thenReturn(List.of());

      // when
      List<PictureTag> pictureTags = pictureTagService.searchTags(albumId, userId, keyword);

      // then
      assertThat(pictureTags).isEmpty();
    }
  }
}
