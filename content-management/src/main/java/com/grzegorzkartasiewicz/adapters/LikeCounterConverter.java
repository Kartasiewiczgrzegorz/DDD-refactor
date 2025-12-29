package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class LikeCounterConverter implements AttributeConverter<LikeCounter, Integer> {

  @Override
  public Integer convertToDatabaseColumn(LikeCounter attribute) {
    return attribute == null ? null : attribute.likeCount();
  }

  @Override
  public LikeCounter convertToEntityAttribute(Integer dbData) {
    return dbData == null ? null : new LikeCounter(dbData);
  }
}
