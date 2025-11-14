package com.grzegorzkartasiewicz.domain.vo;

import static org.junit.jupiter.api.Assertions.*;

import com.grzegorzkartasiewicz.domain.ValidationException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthorIdTest {

  @Test
  void validateShouldNotThrowExceptionWhenAuthorIdIsValid() {
    AuthorId authorId = new AuthorId(UUID.randomUUID());

    authorId.validate();
  }

  @Test
  void validateShouldThrowExceptionWhenAuthorIdIsNull() {
    AuthorId authorId = new AuthorId(null);

    assertThrows(ValidationException.class, authorId::validate);
  }
}