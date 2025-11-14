package com.grzegorzkartasiewicz.app;

import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.Description;
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
        addedPost.getAuthorId().id());
  }
}
