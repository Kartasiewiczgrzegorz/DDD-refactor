package com.grzegorzkartasiewicz.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long id) {

}
