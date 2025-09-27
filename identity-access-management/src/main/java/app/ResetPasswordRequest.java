package app;

import domain.Email;
import domain.Password;

public record ResetPasswordRequest(String email, String password) {

}
