package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.SocialGraphPort;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot test configuration class. This class is necessary for slice tests (such as
 *
 * @DataJpaTest) to find the @SpringBootConfiguration in a multi-module project. It should be placed
 * in a parent package relative to the tested components, so that Spring component scanning can find
 * them.
 */
@SpringBootApplication
public class TestApplication {

  @Bean
  public SocialGraphPort socialGraphPort() {
    return Mockito.mock(SocialGraphPort.class);
  }
}
