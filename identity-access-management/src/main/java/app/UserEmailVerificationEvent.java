package app;

import domain.UserId;
import domain.Verification;

public record UserEmailVerificationEvent(UserId verifiedUserId, Verification verification) {

}
