package com.onebyte.life4cut.user.repository;

import com.onebyte.life4cut.common.annotation.RepositoryTest;
import com.onebyte.life4cut.fixture.UserFixtureFactory;
import com.onebyte.life4cut.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest(UserRepositoryImpl.class)
class UserRepositoryImplTest {

  @Autowired private UserRepositoryImpl userRepositoryImpl;

  @Autowired private UserFixtureFactory userFixtureFactory;

  @Nested
  class findUser {

    @DisplayName("유저를 아이디로 조회한다.")
    @Test
    void findUserById() {
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
      Assertions.assertThat(result.getId()).isEqualTo(user.getId());
    }
  }
}
