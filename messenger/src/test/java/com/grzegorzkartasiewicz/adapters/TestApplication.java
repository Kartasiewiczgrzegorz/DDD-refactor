package com.grzegorzkartasiewicz.adapters;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot test configuration class. This class is necessary for slice tests (such as
 * @DataJpaTest or @SpringBootTest) to find the @SpringBootConfiguration in a multi-module project.
 */
@SpringBootApplication
public class TestApplication {

}
