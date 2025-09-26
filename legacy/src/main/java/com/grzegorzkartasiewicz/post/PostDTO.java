package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.comment.CommentDTO;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.vo.UserId;

import java.util.List;

public class PostDTO {
    private int id;

    private String description;

    private UserId userId;
    private String authorName;
    private String authorSurname;
    private List<CommentDTO> comments;

    public PostDTO() {
    }

    public PostDTO(int id, String description, UserId userId, String authorName, String authorSurname, List<CommentDTO> comments) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.comments = comments;
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
    public List<CommentDTO> getComments() { return comments; }
    public void setComments(List<CommentDTO> comments) { this.comments = comments; }

    static PostDTO toDTO(Post post, UserDTO user, List<CommentDTO> commentDTOs) {
        PostSnapshot postSnapshot = post.getSnapshot();
        return new PostDTO(postSnapshot.getId(), postSnapshot.getDescription(), postSnapshot.getUserId(),
                user.getName(), user.getSurname(), commentDTOs);
    }
}
