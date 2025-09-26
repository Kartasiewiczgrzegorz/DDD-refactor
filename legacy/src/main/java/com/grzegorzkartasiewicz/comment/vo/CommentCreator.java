package com.grzegorzkartasiewicz.comment.vo;

import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.vo.UserId;

public record CommentCreator(String description, PostId postId, UserId userId) {
}
