package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.CommentCreationRequest;
import com.grzegorzkartasiewicz.app.CommentDeleteRequest;
import com.grzegorzkartasiewicz.app.CommentUpdateRequest;
import com.grzegorzkartasiewicz.app.PostCreationRequest;
import com.grzegorzkartasiewicz.app.PostDeleteRequest;
import com.grzegorzkartasiewicz.app.PostResponse;
import com.grzegorzkartasiewicz.app.PostService;
import com.grzegorzkartasiewicz.app.PostUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.security.Principal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Endpoints for managing posts and comments")
class PostController {

  private final PostService postService;

  @Operation(summary = "Add a new post",
      description = "Creates a new post based on the provided data.",
      responses = {
          @ApiResponse(responseCode = "201", description = "Post created successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data")
      })
  @PostMapping
  ResponseEntity<PostResponse> addPost(@RequestBody PostCreationRequest postCreationRequest) {
    PostResponse response = postService.addPost(postCreationRequest);
    return ResponseEntity.created(URI.create("/posts/" + response.id())).body(response);
  }

  @Operation(summary = "Update an existing post",
      description = "Updates a post identified by its ID. Requires authorId for validation.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Post updated successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data"),
          @ApiResponse(responseCode = "404", description = "Post not found"),
          @ApiResponse(responseCode = "403", description = "Unauthorized to edit post")
      })
  @PutMapping("/{postId}")
  ResponseEntity<PostResponse> updatePost(@PathVariable UUID postId,
      @RequestBody PostUpdateRequest postUpdateRequest) {
    if (!postId.equals(postUpdateRequest.id())) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(postService.updatePost(postUpdateRequest));
  }

  @Operation(summary = "Delete a post",
      description = "Deletes a post identified by its ID. Requires authorId for validation.",
      responses = {
          @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
          @ApiResponse(responseCode = "404", description = "Post not found"),
          @ApiResponse(responseCode = "403", description = "Unauthorized to delete post")
      })
  @DeleteMapping("/{postId}")
  ResponseEntity<Void> deletePost(@PathVariable UUID postId,
      @RequestBody PostDeleteRequest postDeleteRequest) {
    if (!postId.equals(postDeleteRequest.id())) {
      return ResponseEntity.badRequest().build();
    }
    postService.deletePost(postDeleteRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Add a comment to a post",
      description = "Adds a new comment to a specified post.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Comment added successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data"),
          @ApiResponse(responseCode = "404", description = "Post not found")
      })
  @PostMapping("/{postId}/comments")
  ResponseEntity<PostResponse> addComment(@PathVariable UUID postId,
      @RequestBody CommentCreationRequest commentCreationRequest) {
    if (!postId.equals(commentCreationRequest.postId())) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(postService.addComment(commentCreationRequest));
  }

  @Operation(summary = "Edit a comment on a post",
      description = "Edits an existing comment on a specified post. Requires authorId for validation.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Comment updated successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data"),
          @ApiResponse(responseCode = "404", description = "Post or comment not found"),
          @ApiResponse(responseCode = "403", description = "Unauthorized to edit comment")
      })
  @PutMapping("/{postId}/comments/{commentId}")
  ResponseEntity<PostResponse> editComment(@PathVariable UUID postId, @PathVariable UUID commentId,
      @RequestBody CommentUpdateRequest commentUpdateRequest) {
    if (!postId.equals(commentUpdateRequest.postId()) || !commentId.equals(
        commentUpdateRequest.commentId())) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(postService.editComment(commentUpdateRequest));
  }

  @Operation(summary = "Remove a comment from a post",
      description = "Removes a comment from a specified post. Requires authorId for validation.",
      responses = {
          @ApiResponse(responseCode = "204", description = "Comment removed successfully"),
          @ApiResponse(responseCode = "404", description = "Post or comment not found"),
          @ApiResponse(responseCode = "403", description = "Unauthorized to remove comment")
      })
  @DeleteMapping("/{postId}/comments/{commentId}")
  ResponseEntity<Void> removeComment(@PathVariable UUID postId, @PathVariable UUID commentId,
      @RequestBody CommentDeleteRequest commentDeleteRequest) {
    if (!postId.equals(commentDeleteRequest.postId()) || !commentId.equals(
        commentDeleteRequest.commentId())) {
      return ResponseEntity.badRequest().build();
    }
    postService.removeComment(commentDeleteRequest);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Like a post",
      description = "Increases the like count of a post.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Post liked successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "404", description = "Post not found")
      })
  @PatchMapping("/{postId}/like")
  ResponseEntity<PostResponse> likePost(@PathVariable UUID postId, Principal principal) {
    return ResponseEntity.ok(postService.likePost(postId, UUID.fromString(principal.getName())));
  }

  @Operation(summary = "Unlike a post",
      description = "Decreases the like count of a post.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Post unliked successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "404", description = "Post not found")
      })
  @PatchMapping("/{postId}/unlike")
  ResponseEntity<PostResponse> unlikePost(@PathVariable UUID postId, Principal principal) {
    return ResponseEntity.ok(postService.unlikePost(postId, UUID.fromString(principal.getName())));
  }

  @Operation(summary = "Like a comment",
      description = "Increases the like count of a comment on a post.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Comment liked successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "404", description = "Post or comment not found")
      })
  @PatchMapping("/{postId}/comments/{commentId}/like")
  ResponseEntity<PostResponse> likeComment(@PathVariable UUID postId,
      @PathVariable UUID commentId, Principal principal) {
    return ResponseEntity.ok(
        postService.likeComment(postId, commentId, UUID.fromString(principal.getName())));
  }

  @Operation(summary = "Unlike a comment",
      description = "Decreases the like count of a comment on a post.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Comment unliked successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = PostResponse.class))),
          @ApiResponse(responseCode = "404", description = "Post or comment not found")
      })
  @PatchMapping("/{postId}/comments/{commentId}/unlike")
  ResponseEntity<PostResponse> unlikeComment(@PathVariable UUID postId,
      @PathVariable UUID commentId, Principal principal) {
    return ResponseEntity.ok(
        postService.unlikeComment(postId, commentId, UUID.fromString(principal.getName())));
  }
}
