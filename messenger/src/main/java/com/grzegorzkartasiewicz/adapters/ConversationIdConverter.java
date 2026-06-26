package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.ConversationId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
class ConversationIdConverter implements AttributeConverter<ConversationId, UUID> {

  @Override
  public UUID convertToDatabaseColumn(ConversationId attribute) {
    return attribute == null ? null : attribute.id();
  }

  @Override
  public ConversationId convertToEntityAttribute(UUID dbData) {
    return dbData == null ? null : new ConversationId(dbData);
  }
}
