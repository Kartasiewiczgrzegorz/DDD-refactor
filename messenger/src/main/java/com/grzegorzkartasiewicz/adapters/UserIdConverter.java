package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.UserId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
class UserIdConverter implements AttributeConverter<UserId, UUID> {

  @Override
  public UUID convertToDatabaseColumn(UserId attribute) {
    return attribute == null ? null : attribute.id();
  }

  @Override
  public UserId convertToEntityAttribute(UUID dbData) {
    return dbData == null ? null : new UserId(dbData);
  }
}
