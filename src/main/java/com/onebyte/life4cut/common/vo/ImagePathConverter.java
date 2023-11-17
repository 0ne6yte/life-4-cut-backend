package com.onebyte.life4cut.common.vo;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ImagePathConverter implements AttributeConverter<ImagePath, String> {
  @Override
  public String convertToDatabaseColumn(ImagePath attribute) {
    if (attribute == null) {
      return null;
    }
    return attribute.getValue();
  }

  @Override
  public ImagePath convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }

    return ImagePath.of(dbData);
  }
}
