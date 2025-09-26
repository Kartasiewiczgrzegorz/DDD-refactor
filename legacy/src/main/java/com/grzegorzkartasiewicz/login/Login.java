package com.grzegorzkartasiewicz.login;


import com.grzegorzkartasiewicz.login.vo.LoginCreator;
import com.grzegorzkartasiewicz.user.vo.UserId;


class Login {

    static Login restore(LoginSnapshot snapshot) {
        return new Login(
                snapshot.getId(),
                snapshot.getNick(),
                snapshot.getPassword(),
                snapshot.getEmail(),
                snapshot.getUserId()
        );
    }

    static Login createFrom(final LoginCreator source) {
        return new Login(
                0,
                source.nick(),
                source.password(),
                source.email(),
                null
        );
    }

    private int id;

    private String nick;

    private String password;

    private String email;

    private UserId userId;

    private Login(int id, String nick, String password, String email, UserId userId) {
        this.id = id;
        this.nick = nick;
        this.password = password;
        this.email = email;
        this.userId = userId;
    }

    LoginSnapshot getSnapshot() {
        return new LoginSnapshot(id, nick, password, email, userId);
    }

    boolean hasMatchingPassword(String password) {
        return this.password.equals(password);
    }

    void assignUser(int userId) {
        if (this.userId == null) {
            this.userId = new UserId(userId);
        }
    }
}
