package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.SocialService;
import com.grzegorzkartasiewicz.domain.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SocialGraphConfiguration {

  @Bean
  SocialService socialService(UserRepository userRepository) {
    return new SocialService(userRepository);
  }
}
