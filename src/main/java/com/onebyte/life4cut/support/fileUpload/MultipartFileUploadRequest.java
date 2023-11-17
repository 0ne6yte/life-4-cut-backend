package com.onebyte.life4cut.support.fileUpload;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class MultipartFileUploadRequest implements FileUploadRequest {

  private static final String DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_OCTET_STREAM_VALUE;
  private static final String DEFAULT_FILE_NAME = "default_file_name";

  @Nonnull private final MultipartFile multipartFile;

  @Nonnull private final String bucket;

  private MultipartFileUploadRequest(@Nonnull MultipartFile multipartFile, @Nonnull String bucket) {
    this.multipartFile = multipartFile;
    this.bucket = bucket;
  }

  public static FileUploadRequest of(@Nonnull MultipartFile multipartFile, @Nonnull String bucket) {
    return new MultipartFileUploadRequest(multipartFile, bucket);
  }

  @Nonnull
  @Override
  public String getBucket() {
    return this.bucket;
  }

  @Override
  public InputStream getInputStream() {
    try {
      return multipartFile.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Nonnull
  @Override
  public String getFileName() {
    if (StringUtils.hasText(multipartFile.getOriginalFilename())) {
      return String.format("%s/%s", UUID.randomUUID(), multipartFile.getOriginalFilename());
    }
    return String.format("%s/%s", UUID.randomUUID(), DEFAULT_FILE_NAME);
  }

  @Nonnull
  @Override
  public String getContentType() {
    if (StringUtils.hasText(multipartFile.getContentType())) {
      return multipartFile.getContentType();
    }
    return DEFAULT_CONTENT_TYPE;
  }

  @Override
  public long getContentLength() {
    return multipartFile.getSize();
  }
}
