package com.onebyte.life4cut.support.fileUpload;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

class MultipartFileUploadRequestTest {

  @Nested
  class GetBucket {
    @Test
    @DisplayName("bucket 이름을 반환한다")
    void shouldReturnBucketName() {
      // given
      String bucket = "bucket";
      MultipartFile file = new MockMultipartFile("test.txt", "test".getBytes());

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);
      // when
      String result = multipartFileUploadRequest.getBucket();

      // then
      assertThat(result).isEqualTo(bucket);
    }
  }

  @Nested
  class GetInputStream {
    @Test
    @DisplayName("MultipartFile의 InputStream을 반환한다")
    void shouldReturnInputStream() throws IOException {
      // given
      String bucket = "bucket";
      MultipartFile file = new MockMultipartFile("test.txt", "test".getBytes());

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);
      // when
      InputStream result = multipartFileUploadRequest.getInputStream();

      // then
      assertThat(result).isNotNull();
      assertThat(result.readAllBytes()).isEqualTo("test".getBytes());
    }
  }

  @Nested
  class GetFileName {
    Pattern UUID_REGEX =
        Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    @Test
    @DisplayName("MultipartFile의 기존 파일 이름이 있으면, 파일이름과 UUID를 반환한다")
    void shouldReturnFileName() {
      // given
      String bucket = "bucket";

      String name = "test.txt";
      String originalFileName = "originalName.txt";
      String contentType = "text/plain";
      byte[] content = "test".getBytes();
      MultipartFile file = new MockMultipartFile(name, originalFileName, contentType, content);

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);
      // when
      String result = multipartFileUploadRequest.getFileName();

      // then
      String[] values = result.split("/");
      assertThat(UUID_REGEX.matcher(values[0]).matches()).isTrue();
      assertThat(result).isEqualTo(values[0] + "/" + originalFileName);
    }

    @Test
    @DisplayName("MultipartFile의 파일 이름이 없으면 UUID와 기본 파일 이름을 반환한다")
    void shouldReturnFileNameWithUUID() {
      // given
      String bucket = "bucket";

      String name = "test.txt";
      byte[] content = "test".getBytes();
      MultipartFile file = new MockMultipartFile(name, content);

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);

      // when
      String result = multipartFileUploadRequest.getFileName();

      // then
      String[] values = result.split("/");
      assertThat(UUID_REGEX.matcher(values[0]).matches()).isTrue();
      assertThat(result).isEqualTo(values[0] + "/" + "default_file_name");
    }
  }

  @Nested
  class GetContentType {
    @Test
    @DisplayName("MultipartFile의 ContentType을 반환한다")
    void shouldReturnContentType() {
      // given
      String bucket = "bucket";

      String name = "test.txt";
      String originalFileName = "originalName.txt";
      String contentType = "text/plain";
      byte[] content = "test".getBytes();
      MultipartFile file = new MockMultipartFile(name, originalFileName, contentType, content);

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);

      // when
      String result = multipartFileUploadRequest.getContentType();

      // then
      assertThat(result).isEqualTo(contentType);
    }

    @Test
    @DisplayName("MultipartFile의 ContentType이 없으면 기본 ContentType을 반환한다")
    void shouldReturnDefaultContentType() {
      // given
      String bucket = "bucket";

      String name = "test.txt";
      byte[] content = "test".getBytes();
      MultipartFile file = new MockMultipartFile(name, content);

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);

      // when
      String result = multipartFileUploadRequest.getContentType();

      // then
      assertThat(result).isEqualTo("application/octet-stream");
    }
  }

  @Nested
  class GetContentLength {
    @Test
    @DisplayName("MultipartFile의 ContentLength를 반환한다")
    void shouldReturnContentLength() {
      // given
      String bucket = "bucket";

      String name = "test.txt";
      byte[] content = "test".getBytes();
      MultipartFile file = new MockMultipartFile(name, content);

      FileUploadRequest multipartFileUploadRequest = MultipartFileUploadRequest.of(file, bucket);

      // when
      long result = multipartFileUploadRequest.getContentLength();

      // then
      assertThat(result).isEqualTo(content.length);
    }
  }
}
