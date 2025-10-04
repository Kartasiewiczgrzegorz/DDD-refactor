package com.grzegorzkartasiewicz.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

class UserTest {

  @Test
  void verifyShouldChangeVerificationToVerified() {
    User testUser = getTestUser();

    assertThat(testUser.getVerification()).isEqualTo(Verification.UNVERIFIED);

    testUser.verify();

    assertThat(testUser.getVerification()).isEqualTo(Verification.VERIFIED);
  }

  @Test
  void verifyPasswordShouldNotThrowExceptionIfPasswordIsEqual() {
    User testUser = getTestUser();

    testUser.verifyPassword("Passw0rd$#");
  }

  @Test
  void verifyPasswordShouldThrowExceptionIfPasswordIsNotEqual() {
    User testUser = getTestUser();

    assertThrows(PasswordDoesNotMatchException.class, () -> testUser.verifyPassword("NotEqualPassword"));
  }

  @Test
  void increaseInvalidLogInCounterShouldIncreaseValueByOne() {
    User testUser = getTestUser();
    int initialValue = testUser.getInvalidLogInCounter().counter();

    testUser.recordFailedLoginAttempt();

    assertThat(testUser.getInvalidLogInCounter().counter()).isEqualTo(initialValue + 1);
  }

  @Test
  void recordFailedLoginAttemptHitFive() {
    User testUser = getTestUser();

    assertThat(testUser.getBlocked()).isEqualTo(Blocked.NOT_BLOCKED);

    for (int i = 1; i <= 6; i++) {
      testUser.recordFailedLoginAttempt();
    }

    assertThat(testUser.getBlocked()).isEqualTo(Blocked.BLOCKED);
  }

  @Test
  void isBlockedShouldReturnTrueIfBlocked() {
    User testUser = getTestUser();

    for (int i = 1; i <= 6; i++) {
      testUser.recordFailedLoginAttempt();
    }


    assertThat(testUser.isBlocked()).isTrue();
  }

  @Test
  void isBlockedShouldReturnFalesIfNotBlocked() {
    User testUser = getTestUser();

    assertThat(testUser.isBlocked()).isFalse();
  }

  private static @NotNull User getTestUser() {
    return new User(new Name("name", "surname"), new Email("email@test.com"),
        new Password("Passw0rd$#"));
  }

}