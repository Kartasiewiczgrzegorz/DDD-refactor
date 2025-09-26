package com.grzegorzkartasiewicz.login;

import com.grzegorzkartasiewicz.SpringDomainEventPublisher;
import com.grzegorzkartasiewicz.user.UserFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoginConfigurator {

    @Bean
    LoginFacade loginFacade(final LoginRepository loginRepository, final UserFacade userFacade) {
        return new LoginFacade(loginRepository, userFacade);
    }
}
