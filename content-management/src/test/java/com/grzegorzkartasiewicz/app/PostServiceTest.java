package com.grzegorzkartasiewicz.app;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.grzegorzkartasiewicz.domain.Post;
import com.grzegorzkartasiewicz.domain.PostRepository;
import com.grzegorzkartasiewicz.domain.ValidationException;
import com.grzegorzkartasiewicz.domain.vo.AuthorId;
import com.grzegorzkartasiewicz.domain.vo.Description;
import com.grzegorzkartasiewicz.domain.vo.LikeCounter;
import com.grzegorzkartasiewicz.domain.vo.PostId;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

  private static final UUID AUTHOR_ID = UUID.randomUUID();
  private static final String VALID_DESCRIPTION = "Valid description";
  @Mock
  private PostRepository postRepository;
  @InjectMocks
  private PostService postService;

  private Post testPost;

  @BeforeEach
  void setUp() {
    Description description = new Description(VALID_DESCRIPTION);
    AuthorId authorId = new AuthorId(AUTHOR_ID);
    testPost = new Post(new PostId(UUID.randomUUID()), description,
        authorId, new LikeCounter(0), new ArrayList<>());
  }

  @Test
  @DisplayName("add post should create and save post when given valid description and author")
  void addPost_shouldCreateAndSavePostWhenGivenValidDescriptionAndAuthor() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(VALID_DESCRIPTION,
        AUTHOR_ID);

    when(postRepository.save(any(Post.class))).thenReturn(testPost);

    PostResponse postResponse = postService.addPost(postCreationRequest);

    assertThat(postResponse.authorId()).isEqualTo(postCreationRequest.authorId());
    assertThat(postResponse.description()).isEqualTo(postCreationRequest.description());
  }

  @Test
  @DisplayName("add post should throw validation exception when description is invalid")
  void addPost_shouldThrowValidationExceptionWhenDescriptionIsInvalid() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(" ",
        AUTHOR_ID);


    assertThrows(ValidationException.class, () -> postService.addPost(postCreationRequest));
  }

  @Test
  @DisplayName("add post should throw validation exception when description is null")
  void addPost_shouldThrowValidationExceptionWhenDescriptionIsNull() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(null,
        AUTHOR_ID);

    assertThrows(ValidationException.class, () -> postService.addPost(postCreationRequest));
  }

  @Test
  @DisplayName("add post should throw validation exception when author id is null")
  void addPost_shouldThrowValidationExceptionWhenAuthorIdIsNull() {
    PostCreationRequest postCreationRequest = new PostCreationRequest(VALID_DESCRIPTION,
        null);

    assertThrows(ValidationException.class, () -> postService.addPost(postCreationRequest));
  }
}