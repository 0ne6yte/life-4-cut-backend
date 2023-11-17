package com.onebyte.life4cut.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer(
      ImagePathSerializer imagePathSerializer) {
    return builder -> builder.serializers(imagePathSerializer);
  }
}
