package com.grzegorzkartasiewicz.comment.vo;

public record CommentId(int id) {
    public CommentId() {
        this(0);
    }
}
