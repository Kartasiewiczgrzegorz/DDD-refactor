package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.Description;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class DescriptionConverter implements AttributeConverter<Description, String> {

  @Override
  public String convertToDatabaseColumn(Description attribute) {
    return attribute == null ? null : attribute.text();
  }

  @Override
  public Description convertToEntityAttribute(String dbData) {
    return dbData == null ? null : new Description(dbData);
  }
}
