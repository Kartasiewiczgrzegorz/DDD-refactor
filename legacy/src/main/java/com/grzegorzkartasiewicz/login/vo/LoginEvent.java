package com.grzegorzkartasiewicz.login.vo;

import com.grzegorzkartasiewicz.DomainEvent;

import java.time.ZonedDateTime;

public class LoginEvent implements DomainEvent {
    private LoginId loginId;
    private State state;
    private LoginData data;

    public LoginEvent(LoginId loginId, State state, LoginData data) {
        this.loginId = loginId;
        this.state = state;
        this.data = data;
    }

    public LoginId getLoginId() {
        return loginId;
    }

    public State getState() {
        return state;
    }

    public LoginData getData() {
        return data;
    }

    public static record LoginData(String nick, String password, String email) {
    }
}
