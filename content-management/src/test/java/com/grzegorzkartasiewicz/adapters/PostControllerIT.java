package com.grzegorzkartasiewicz.adapters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grzegorzkartasiewicz.app.CommentCreationRequest;
import com.grzegorzkartasiewicz.app.CommentDeleteRequest;
import com.grzegorzkartasiewicz.app.CommentUpdateRequest;
import com.grzegorzkartasiewicz.app.PostCreationRequest;
import com.grzegorzkartasiewicz.app.PostDeleteRequest;
import com.grzegorzkartasiewicz.app.PostResponse;
import com.grzegorzkartasiewicz.app.PostService;
import com.grzegorzkartasiewicz.app.PostUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class PostControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private PostService postService;

  @Test
  @DisplayName("should add post and return 201 Created")
  void shouldAddPost() throws Exception {
    // given
    UUID authorId = UUID.randomUUID();
    PostCreationRequest request = new PostCreationRequest("Test description", authorId);

    // when & then
    mockMvc.perform(post("/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.description").value("Test description"))
        .andExpect(jsonPath("$.authorId").value(authorId.toString()))
        .andExpect(jsonPath("$.likeCount").value(0));
  }

  @Test
  @DisplayName("should update post and return 200 OK")
  void shouldUpdatePost() throws Exception {
    // given
    UUID authorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Original description", authorId));

    PostUpdateRequest updateRequest = new PostUpdateRequest(createdPost.id(), "Updated description",
        authorId);

    // when & then
    mockMvc.perform(put("/posts/{postId}", createdPost.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description").value("Updated description"));
  }

  @Test
  @DisplayName("should return 400 Bad Request when update post ID mismatch")
  void shouldReturnBadRequestWhenUpdatePostIdMismatch() throws Exception {
    // given
    UUID postId = UUID.randomUUID();
    UUID differentId = UUID.randomUUID();
    PostUpdateRequest request = new PostUpdateRequest(differentId, "Updated description",
        UUID.randomUUID());

    // when & then
    mockMvc.perform(put("/posts/{postId}", postId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should delete post and return 204 No Content")
  void shouldDeletePost() throws Exception {
    // given
    UUID authorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("To be deleted", authorId));

    PostDeleteRequest deleteRequest = new PostDeleteRequest(createdPost.id(), authorId);

    // when & then
    mockMvc.perform(delete("/posts/{postId}", createdPost.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(deleteRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("should add comment to post")
  void shouldAddComment() throws Exception {
    // given
    UUID postAuthorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post with comment", postAuthorId));

    UUID commentAuthorId = UUID.randomUUID();
    CommentCreationRequest commentRequest = new CommentCreationRequest(createdPost.id(),
        "Nice post!", commentAuthorId);

    // when & then
    mockMvc.perform(post("/posts/{postId}/comments", createdPost.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(commentRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.comments[0].description").value("Nice post!"))
        .andExpect(jsonPath("$.comments[0].authorId").value(commentAuthorId.toString()));
  }

  @Test
  @DisplayName("should edit comment")
  void shouldEditComment() throws Exception {
    // given
    UUID postAuthorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post with comment", postAuthorId));

    UUID commentAuthorId = UUID.randomUUID();
    PostResponse postWithComment = postService.addComment(
        new CommentCreationRequest(createdPost.id(), "Original comment", commentAuthorId));
    UUID commentId = postWithComment.comments().get(0).id();

    CommentUpdateRequest updateRequest = new CommentUpdateRequest(createdPost.id(), commentId,
        "Updated comment", commentAuthorId);

    // when & then
    mockMvc.perform(
            put("/posts/{postId}/comments/{commentId}", createdPost.id(), commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.comments[0].description").value("Updated comment"));
  }

  @Test
  @DisplayName("should remove comment")
  void shouldRemoveComment() throws Exception {
    // given
    UUID postAuthorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post with comment", postAuthorId));

    UUID commentAuthorId = UUID.randomUUID();
    PostResponse postWithComment = postService.addComment(
        new CommentCreationRequest(createdPost.id(), "Comment to remove", commentAuthorId));
    UUID commentId = postWithComment.comments().get(0).id();

    CommentDeleteRequest deleteRequest = new CommentDeleteRequest(createdPost.id(), commentId,
        commentAuthorId);

    // when & then
    mockMvc.perform(
            delete("/posts/{postId}/comments/{commentId}", createdPost.id(), commentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(deleteRequest)))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("should like post")
  void shouldLikePost() throws Exception {
    // given
    UUID authorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post to like", authorId));

    // when & then
    mockMvc.perform(patch("/posts/{postId}/like", createdPost.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.likeCount").value(1));
  }

  @Test
  @DisplayName("should unlike post")
  void shouldUnlikePost() throws Exception {
    // given
    UUID authorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post to unlike", authorId));
    postService.likePost(createdPost.id()); // like first

    // when & then
    mockMvc.perform(patch("/posts/{postId}/unlike", createdPost.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.likeCount").value(0));
  }

  @Test
  @DisplayName("should like comment")
  void shouldLikeComment() throws Exception {
    // given
    UUID postAuthorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post with comment", postAuthorId));

    UUID commentAuthorId = UUID.randomUUID();
    PostResponse postWithComment = postService.addComment(
        new CommentCreationRequest(createdPost.id(), "Comment to like", commentAuthorId));
    UUID commentId = postWithComment.comments().get(0).id();

    // when & then
    mockMvc.perform(
            patch("/posts/{postId}/comments/{commentId}/like", createdPost.id(), commentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.comments[0].likeCount").value(1));
  }

  @Test
  @DisplayName("should unlike comment")
  void shouldUnlikeComment() throws Exception {
    // given
    UUID postAuthorId = UUID.randomUUID();
    PostResponse createdPost = postService.addPost(
        new PostCreationRequest("Post with comment", postAuthorId));

    UUID commentAuthorId = UUID.randomUUID();
    PostResponse postWithComment = postService.addComment(
        new CommentCreationRequest(createdPost.id(), "Comment to unlike", commentAuthorId));
    UUID commentId = postWithComment.comments().get(0).id();

    postService.likeComment(createdPost.id(), commentId); // like first

    // when & then
    mockMvc.perform(
            patch("/posts/{postId}/comments/{commentId}/unlike", createdPost.id(), commentId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.comments[0].likeCount").value(0));
  }
}