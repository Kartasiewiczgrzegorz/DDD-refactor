package com.grzegorzkartasiewicz.post.vo;

import com.grzegorzkartasiewicz.user.vo.UserId;

public record PostCreator(String description, UserId userId) {
}
