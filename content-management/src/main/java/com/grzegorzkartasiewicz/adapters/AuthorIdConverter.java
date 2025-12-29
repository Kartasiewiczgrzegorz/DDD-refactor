package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
class AuthorIdConverter implements AttributeConverter<AuthorId, UUID> {

  @Override
  public UUID convertToDatabaseColumn(AuthorId attribute) {
    return attribute == null ? null : attribute.id();
  }

  @Override
  public AuthorId convertToEntityAttribute(UUID dbData) {
    return dbData == null ? null : new AuthorId(dbData);
  }
}
