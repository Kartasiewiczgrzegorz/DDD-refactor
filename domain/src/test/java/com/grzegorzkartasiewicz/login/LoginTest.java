package com.grzegorzkartasiewicz.login;

import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class LoginTest {

    @Test
    @DisplayName("should return true for correct password")
    void shouldReturnTrueForCorrectPassword() {
        // given
        var snapshot = new LoginSnapshot(1, "testUser", "correctPassword", "test@test.com", new UserId(1));
        var login = Login.restore(snapshot);

        // when
        boolean result = login.hasMatchingPassword("correctPassword");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("should return false for incorrect password")
    void shouldReturnFalseForIncorrectPassword() {
        // given
        var snapshot = new LoginSnapshot(1, "testUser", "correctPassword", "test@test.com", new UserId(1));
        var login = Login.restore(snapshot);

        // when
        boolean result = login.hasMatchingPassword("incorrectPassword");

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("should return false for null password")
    void shouldReturnFalseForNullPassword() {
        // given
        var snapshot = new LoginSnapshot(1, "testUser", "correctPassword", "test@test.com", new UserId(1));
        var login = Login.restore(snapshot);

        // when
        boolean result = login.hasMatchingPassword(null);

        // then
        assertThat(result).isFalse();
    }
}