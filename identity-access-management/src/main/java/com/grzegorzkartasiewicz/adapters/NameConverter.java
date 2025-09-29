package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.Name;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
class NameConverter implements AttributeConverter<Name, String> {

  @Override
  public String convertToDatabaseColumn(Name attribute) {
    return attribute.name() + " " + attribute.surname();
  }

  @Override
  public Name convertToEntityAttribute(String dbData) {
    String[] splitName = dbData.trim().split(" ");
    return new Name(splitName[0], splitName[1]);
  }
}
