package com.grzegorzkartasiewicz.domain.vo;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record UserId(UUID id) implements Serializable {

}
