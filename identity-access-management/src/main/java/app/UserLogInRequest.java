package app;

import domain.Email;

public record UserLogInRequest(Email email, String password) {

}
