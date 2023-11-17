package com.onebyte.life4cut.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.onebyte.life4cut.common.constants.S3Env;
import com.onebyte.life4cut.common.vo.ImagePath;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImagePathSerializer extends StdSerializer<ImagePath> {

  @Value("${aws.region:ap-northeast-2}")
  private String region;

  private final S3Env s3Env;

  public ImagePathSerializer(S3Env s3Env) {
    super(ImagePath.class);
    this.s3Env = s3Env;
  }

  @Override
  public void serialize(ImagePath value, JsonGenerator gen, SerializerProvider serializers)
      throws IOException {

    gen.writeString(
        "https://" + s3Env.bucket() + ".s3." + region + ".amazonaws.com/" + value.getValue());
  }
}
