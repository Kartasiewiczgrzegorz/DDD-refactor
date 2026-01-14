package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.Email;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class EmailConverter implements AttributeConverter<Email, String> {

  @Override
  public String convertToDatabaseColumn(Email email) {
    return email.email();
  }

  @Override
  public Email convertToEntityAttribute(String dbData) {
    return new Email(dbData);
  }
}
