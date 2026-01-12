package com.grzegorzkartasiewicz.domain.vo;

import com.grzegorzkartasiewicz.domain.ValidationException;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record AuthorId(UUID id) implements Serializable {

  public AuthorId {
    if (id == null) {
      throw new ValidationException("Author id cannot be null");
    }
  }
}
