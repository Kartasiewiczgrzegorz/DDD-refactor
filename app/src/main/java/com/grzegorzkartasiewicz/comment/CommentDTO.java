package com.grzegorzkartasiewicz.comment;

import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.vo.UserId;

public class CommentDTO {
    private int id;

    private String description;

    private PostId postId;

    private UserId userId;
    private String authorName;
    private String authorSurname;

    public CommentDTO(int id, String description, PostId postId, UserId userId, String authorName,
                      String authorSurname) {
        this.id = id;
        this.description = description;
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PostId getPostId() {
        return postId;
    }

    public void setPostId(PostId postId) {
        this.postId = postId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getAuthorSurname() { return authorSurname; }
    public void setAuthorSurname(String authorSurname) { this.authorSurname = authorSurname; }

    static CommentDTO toDTO(Comment comment, UserDTO user) {
        CommentSnapshot commentSnapshot = comment.getSnapshot();
        return new CommentDTO(commentSnapshot.getId(), commentSnapshot.getDescription(), commentSnapshot.getPostId(),
                commentSnapshot.getUserId(), user.getName(), user.getSurname());
    }
}
