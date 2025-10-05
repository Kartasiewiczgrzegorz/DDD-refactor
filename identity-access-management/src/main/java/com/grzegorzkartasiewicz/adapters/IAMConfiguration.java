package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.AuthorizationPort;
import com.grzegorzkartasiewicz.app.UserService;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
class IAMConfiguration {

  @Bean
  UserService userService(final UserRepository userRepository,
      final AuthorizationPort authorizationPort, final DomainEventPublisher domainEventPublisher,
      final PasswordEncoder passwordEncoder) {
    return new UserService(userRepository, authorizationPort, domainEventPublisher,
        passwordEncoder);
  }
}
