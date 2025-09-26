package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.login.vo.LoginId;
import com.grzegorzkartasiewicz.post.vo.PostCreator;
import com.grzegorzkartasiewicz.user.vo.UserCreator;
import com.grzegorzkartasiewicz.user.vo.UserId;


class User {

    static User restore(UserSnapshot snapshot) {
        return new User(
                snapshot.getId(),
                snapshot.getName(),
                snapshot.getSurname(),
                snapshot.getAge()
        );
    }

    static User createFrom(final UserCreator source) {
        return new User(
                0,
                source.name(),
                source.surname(),
                source.age()
        );
    }

    private int id;

    private String name;

    private String surname;
    private int age;

    private User(int id, String name, String surname, int age) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
    }

    UserSnapshot getSnapshot() {
        return new UserSnapshot(id, name, surname, age);
    }

    PostCreator prepareNewPost(final String description) {
        return new PostCreator(description, new UserId(this.id));
    }

}
