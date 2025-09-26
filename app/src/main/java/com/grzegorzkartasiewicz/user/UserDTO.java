package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.comment.vo.CommentId;
import com.grzegorzkartasiewicz.login.vo.LoginId;
import com.grzegorzkartasiewicz.post.vo.PostId;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private int id;
    private String name;
    private String surname;
    private int age;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    static UserDTO toDTO(User user) {
        UserSnapshot userSnapshot = user.getSnapshot();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userSnapshot.getId());
        userDTO.setName(userSnapshot.getName());
        userDTO.setSurname(userSnapshot.getSurname());
        userDTO.setAge(userSnapshot.getAge());
        return userDTO;
    }
}
