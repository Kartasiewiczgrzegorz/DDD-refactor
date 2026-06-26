package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.MessageId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.UUID;

@Converter
class MessageIdConverter implements AttributeConverter<MessageId, UUID> {

  @Override
  public UUID convertToDatabaseColumn(MessageId attribute) {
    return attribute == null ? null : attribute.id();
  }

  @Override
  public MessageId convertToEntityAttribute(UUID dbData) {
    return dbData == null ? null : new MessageId(dbData);
  }
}
