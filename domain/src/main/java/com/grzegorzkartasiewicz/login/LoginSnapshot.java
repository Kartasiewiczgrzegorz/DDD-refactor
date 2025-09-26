package com.grzegorzkartasiewicz.login;

import com.grzegorzkartasiewicz.user.vo.UserId;

class LoginSnapshot {

    private int id;

    private String nick;

    private String password;

    private String email;

    private UserId userId;

    protected LoginSnapshot() {
    }

    LoginSnapshot(int id, String nick, String password, String email, UserId userId) {
        this.id = id;
        this.nick = nick;
        this.password = password;
        this.email = email;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getNick() {
        return nick;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public UserId getUserId() {
        return userId;
    }
}
