package com.onebyte.life4cut.fixture;

import com.navercorp.fixturemonkey.ArbitraryBuilder;
import com.onebyte.life4cut.user.domain.User;
import jakarta.persistence.EntityManager;
import java.util.function.BiConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class UserFixtureFactory extends DefaultFixtureFactory<User> {

  public UserFixtureFactory() {}

  @Autowired
  public UserFixtureFactory(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public ArbitraryBuilder<User> getBuilder(BiConsumer<User, ArbitraryBuilder<User>> builder) {
    return fixtureMonkey.giveMeBuilder(User.class).thenApply(builder);
  }
}
