package app;

import domain.UserId;

public record ResetPasswordEvent(UserId userId, String newPassword) {

}
