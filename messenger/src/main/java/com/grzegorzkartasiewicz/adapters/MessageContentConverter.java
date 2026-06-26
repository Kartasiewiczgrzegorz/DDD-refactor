package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.MessageContent;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class MessageContentConverter implements AttributeConverter<MessageContent, String> {

  @Override
  public String convertToDatabaseColumn(MessageContent attribute) {
    return attribute == null ? null : attribute.value();
  }

  @Override
  public MessageContent convertToEntityAttribute(String dbData) {
    return dbData == null ? null : new MessageContent(dbData);
  }
}
