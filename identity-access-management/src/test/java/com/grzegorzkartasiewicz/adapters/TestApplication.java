package com.grzegorzkartasiewicz.adapters;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot test configuration class.
 * This class is necessary for slice tests (such as @DataJpaTest)
 * to find the @SpringBootConfiguration in a multi-module project.
 * It should be placed in a parent package relative to the tested components,
 * so that Spring component scanning can find them.
 */
@SpringBootApplication
public class TestApplication {
}
