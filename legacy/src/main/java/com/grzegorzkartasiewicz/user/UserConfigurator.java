package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.post.PostFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
class UserConfigurator {

    @Bean
    UserFacade userFacade(final UserRepository userRepository, @Lazy final PostFacade postFacade) {
        return new UserFacade(userRepository, postFacade);
    }
}
