package app;

import domain.User;

public interface AuthorizationPort {

  Token generateToken(User user);

}
