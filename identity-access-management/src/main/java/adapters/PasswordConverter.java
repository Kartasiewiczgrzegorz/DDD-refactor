package adapters;

import domain.Email;
import domain.Password;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class PasswordConverter implements AttributeConverter<Password, String> {

  @Override
  public String convertToDatabaseColumn(Password password) {
    return password.password();
  }

  @Override
  public Password convertToEntityAttribute(String dbData) {
    return new Password(dbData);
  }
}
