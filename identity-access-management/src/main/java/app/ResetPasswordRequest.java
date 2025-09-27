package app;

import domain.Email;
import domain.Password;

public record ResetPasswordRequest(Email email, String password) {

}
