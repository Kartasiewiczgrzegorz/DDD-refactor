package adapters;

import domain.Email;
import domain.InvalidLogInCounter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class InvalidLogInCounterConverter implements
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
