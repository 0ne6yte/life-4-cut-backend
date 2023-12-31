package com.onebyte.life4cut.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

  private final HttpStatus status;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    status = errorCode.getStatus();
  }
}
