package com.grzegorzkartasiewicz.adapters;

import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public abstract class AbstractIT {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

  static {
    postgres.start();
  }
}
