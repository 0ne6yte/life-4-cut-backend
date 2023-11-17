package com.onebyte.life4cut.config;

import com.onebyte.life4cut.common.constants.S3Env;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestS3EnvConfiguration {
  @Bean
  public S3Env s3Env() {
    return new S3Env("test-bucket");
  }
}
