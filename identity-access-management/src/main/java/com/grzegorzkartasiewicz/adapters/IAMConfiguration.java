package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.AuthorizationPort;
import com.grzegorzkartasiewicz.app.UserService;
import com.grzegorzkartasiewicz.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IAMConfiguration {

  @Bean
  UserService userService(final UserRepository userRepository,
      final AuthorizationPort authorizationPort) {
    return new UserService(userRepository, authorizationPort);
  }
}
