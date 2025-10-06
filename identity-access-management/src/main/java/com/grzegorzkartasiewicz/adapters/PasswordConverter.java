package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.Password;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class PasswordConverter implements AttributeConverter<Password, String> {

  @Override
  public String convertToDatabaseColumn(Password password) {
    return password.password();
  }

  @Override
  public Password convertToEntityAttribute(String dbData) {
    return new Password(dbData);
  }
}
