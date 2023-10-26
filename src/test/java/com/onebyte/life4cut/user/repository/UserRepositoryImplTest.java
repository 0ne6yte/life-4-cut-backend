package com.onebyte.life4cut.user.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import com.onebyte.life4cut.auth.dto.OAuthInfo;
import com.onebyte.life4cut.common.annotation.RepositoryTest;
import com.onebyte.life4cut.common.constants.OAuthType;
import com.onebyte.life4cut.fixture.UserFixtureFactory;
import com.onebyte.life4cut.user.domain.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest(UserRepositoryImpl.class)
class UserRepositoryImplTest {

  @Autowired private UserRepositoryImpl userRepositoryImpl;

  @Autowired private UserFixtureFactory userFixtureFactory;

  @Nested
  class findUserById {

    @DisplayName("유저를 아이디로 조회한다.")
    @Test
    void success() {
      // given
      String email = "pythonstrup@gmail.com";
      String nickname = "bell";
      User user =
          userFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
                builder.set("email", email);
                builder.set("nickname", nickname);
              });

      // when
      User result = userRepositoryImpl.findUser(user.getId()).get();

      // then
      assertThat(result.getId()).isEqualTo(user.getId());
    }

    @DisplayName("유저를 아이디로 조회했는데 존재하지 않으면 빈 Optional을 반환한다.")
    @Test
    void empty() {
      // given
      long id = 1L;

      // when
      Optional<User> result = userRepositoryImpl.findUser(id);

      // then
      assertThat(result).isEmpty();
    }
  }

  @Nested
  class findUserByNickname {
    @DisplayName("닉네임으로 유저를 조회한다.")
    @Test
    void success() {
      // given
      String email = "pythonstrup@gmail.com";
      String nickname = "bell";
      User user =
          userFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
                builder.set("email", email);
                builder.set("nickname", nickname);
              });

      // when
      User result = userRepositoryImpl.findUserByNickname(nickname).get();

      // then
      assertThat(result.getId()).isEqualTo(user.getId());
    }

    @DisplayName("닉네임으로 유저를 조회했는데 존재하지 않으면 빈 Optional 객체를 반환한다.")
    @Test
    void empty() {
      // given
      String nickname = "bell";

      // when
      Optional<User> result = userRepositoryImpl.findUserByNickname(nickname);

      // then
      assertThat(result).isEmpty();
    }
  }

  @Nested
  class findUserByOAuthInfo {

    @DisplayName("OAuth 정보로 유저를 조회할 수 있다.")
    @Test
    void success() {
      // given
      String email = "pythonstrup@gmail.com";
      String nickname = "bell";
      OAuthType oAuthType = OAuthType.GOOGLE_LOGIN;
      String oAuthId = "12345";
      User user =
          userFixtureFactory.save(
              (entity, builder) -> {
                builder.setNull("deletedAt");
                builder.set("email", email);
                builder.set("nickname", nickname);
                builder.set("oauthType", oAuthType.getType());
                builder.set("oauthId", oAuthId);
              });

      // when
      OAuthInfo mockOAuthInfo = spy(OAuthInfo.class); // 생성자를 private으로 해둠 => reflection vs Mockito
      when(mockOAuthInfo.getOauthType()).thenReturn(oAuthType);
      when(mockOAuthInfo.getOauthId()).thenReturn(oAuthId);
      List<User> result = userRepositoryImpl.findUserByOAuthInfo(mockOAuthInfo);
      // todo. 리팩토링 필요 => 메소드 자체가 OAuthInfo 객체에 대한 의존성이 너무 높다. => 테스트가 불편해진다.
      // todo. 그냥 String 형태의 인자를 넘기는 것이 좋을 것 같다.

      // then
      assertThat(result.get(0).getId()).isEqualTo(user.getId());
    }
  }
}
