package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.domain.vo.InvalidLogInCounter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
class InvalidLogInCounterConverter implements
    AttributeConverter<InvalidLogInCounter, Integer> {

  @Override
  public Integer convertToDatabaseColumn(InvalidLogInCounter invalidLogInCounter) {
    return invalidLogInCounter.counter();
  }

  @Override
  public InvalidLogInCounter convertToEntityAttribute(Integer dbData) {
    return new InvalidLogInCounter(dbData);
  }
}
