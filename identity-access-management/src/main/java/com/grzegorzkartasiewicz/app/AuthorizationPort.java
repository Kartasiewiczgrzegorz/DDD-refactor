package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.User;

public interface AuthorizationPort {

  Token generateToken(User user);

}
