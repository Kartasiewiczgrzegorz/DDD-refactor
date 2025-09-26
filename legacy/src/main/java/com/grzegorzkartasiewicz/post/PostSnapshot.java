package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.user.vo.UserId;

class PostSnapshot {

    private int id;

    private String description;

    private UserId userId;

    protected PostSnapshot() {
    }

    PostSnapshot(int id, String description, UserId userId) {
        this.id = id;
        this.description = description;
        this.userId = userId;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public UserId getUserId() {
        return userId;
    }

}
