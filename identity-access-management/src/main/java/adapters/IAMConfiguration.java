package adapters;

import app.AuthorizationPort;
import app.UserService;
import domain.UserRepository;
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
