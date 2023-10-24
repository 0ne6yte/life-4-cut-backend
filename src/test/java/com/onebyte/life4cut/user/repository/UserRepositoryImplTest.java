package com.onebyte.life4cut.user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.onebyte.life4cut.common.annotation.RepositoryTest;
import com.onebyte.life4cut.fixture.UserFixtureFactory;
import com.onebyte.life4cut.user.domain.User;
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
}
