package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Comment;
import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.CommentId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;

  public PostResponse addPost(PostCreationRequest postCreationRequest) {
    Description description = new Description(postCreationRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(postCreationRequest.authorId());
    authorId.validate();
    Post postToAdd = Post.createNew(description,
        authorId);

    Post addedPost = postRepository.save(postToAdd);

    return new PostResponse(addedPost.getId().id(), addedPost.getDescription().text(),
        addedPost.getAuthorId().id(), mapCommentsToResponse(addedPost.getComments()));
  }

  public PostResponse updatePost(PostUpdateRequest postUpdateRequest) {
    Description description = new Description(postUpdateRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(postUpdateRequest.authorId());
    authorId.validate();
    Post postToEdit = postRepository.findPostById(new PostId(postUpdateRequest.id())).orElseThrow(
        () -> new PostNotExists(
            String.format("Post with given ID: %s does not exist", postUpdateRequest.id())));

    postToEdit.edit(description, authorId);

    Post editedPost = postRepository.save(postToEdit);

    return new PostResponse(editedPost.getId().id(), editedPost.getDescription().text(),
        editedPost.getAuthorId().id(), mapCommentsToResponse(postToEdit.getComments()));
  }

  public void deletePost(PostDeleteRequest postDeleteRequest) {
    AuthorId authorId = new AuthorId(postDeleteRequest.authorId());
    authorId.validate();

    Post postToDelete = postRepository.findPostById(new PostId(postDeleteRequest.id())).orElseThrow(
        () -> new PostNotExists(
            String.format("Post with given ID: %s does not exist", postDeleteRequest.id())));

    postToDelete.validateIfAuthorIsTheSame(postToDelete.getAuthorId(), authorId);

    postRepository.delete(postToDelete);
  }

  public PostResponse addComment(CommentCreationRequest commentCreationRequest) {
    Description description = new Description(commentCreationRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(commentCreationRequest.authorId());
    authorId.validate();

    Post postToEdit = postRepository.findPostById(new PostId(commentCreationRequest.postId()))
        .orElseThrow(
            () -> new PostNotExists(
                String.format("Post with given ID: %s does not exist",
                    commentCreationRequest.postId())));

    postToEdit.addComment(description, authorId);

    Post editedPost = postRepository.save(postToEdit);
    return new PostResponse(editedPost.getId().id(), editedPost.getDescription().text(),
        editedPost.getAuthorId().id(), mapCommentsToResponse(postToEdit.getComments()));
  }

  private List<CommentResponse> mapCommentsToResponse(List<Comment> comments) {
    return comments.stream().map(
            comment -> new CommentResponse(comment.getId().id(), comment.getDescription().text(),
                comment.getAuthorId().id()))
        .toList();
  }

  public PostResponse editComment(CommentUpdateRequest commentUpdateRequest) {
    Description description = new Description(commentUpdateRequest.description());
    description.validate();
    AuthorId authorId = new AuthorId(commentUpdateRequest.authorId());
    authorId.validate();

    Post postToEdit = postRepository.findPostById(new PostId(commentUpdateRequest.postId()))
        .orElseThrow(
            () -> new PostNotExists(
                String.format("Post with given ID: %s does not exist",
                    commentUpdateRequest.postId())));

    postToEdit.editComment(new CommentId(commentUpdateRequest.commentId()), description, authorId);

    Post editedPost = postRepository.save(postToEdit);
    return new PostResponse(editedPost.getId().id(), editedPost.getDescription().text(),
        editedPost.getAuthorId().id(), mapCommentsToResponse(postToEdit.getComments()));
  }

  public void removeComment(CommentDeleteRequest commentDeleteRequest) {
    AuthorId authorId = new AuthorId(commentDeleteRequest.authorId());
    authorId.validate();

    Post postToDelete = postRepository.findPostById(new PostId(commentDeleteRequest.postId())).orElseThrow(
        () -> new PostNotExists(
            String.format("Post with given ID: %s does not exist", commentDeleteRequest.postId())));

    postToDelete.removeComment(new CommentId(commentDeleteRequest.commentId()), authorId);

    postRepository.save(postToDelete);
  }
}
