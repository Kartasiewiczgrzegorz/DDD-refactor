package com.grzegorzkartasiewicz.login;

import com.grzegorzkartasiewicz.login.vo.LoginCreator;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.UserFacade;
import com.grzegorzkartasiewicz.user.vo.UserCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoginFacade {
    private static final Logger logger = LoggerFactory.getLogger(LoginFacade.class);
    private final LoginRepository repository;
    private final UserFacade userFacade;

    LoginFacade(LoginRepository repository, UserFacade userFacade) {
        this.repository = repository;
        this.userFacade = userFacade;
    }

    public UserDTO signInUser(LoginDTO loginData) {
        UserCreator userCreator = new UserCreator(
                loginData.getUser().getName(),
                loginData.getUser().getSurname(),
                loginData.getUser().getAge()
        );
        UserDTO createdUser = userFacade.createUser(userCreator);

        LoginCreator loginCreator = new LoginCreator(
                loginData.getNick(),
                loginData.getPassword(),
                loginData.getEmail()
        );
        Login login = Login.createFrom(loginCreator);
        login.assignUser(createdUser.getId());
        repository.save(login);

        return createdUser;
    }

    public UserDTO logInUser(String nick, String password) {
        logger.info("Trying to log in user!");
        return repository.findByNick(nick)
                .filter(login -> login.hasMatchingPassword(password))
                .map(login -> userFacade.getUser(login.getSnapshot().getUserId()))
                .orElse(null);
    }

}
