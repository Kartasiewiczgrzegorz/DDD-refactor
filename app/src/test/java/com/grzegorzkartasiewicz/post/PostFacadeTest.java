package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.DomainEventPublisher;
import com.grzegorzkartasiewicz.comment.CommentFacade;
import com.grzegorzkartasiewicz.comment.vo.CommentCreator;
import com.grzegorzkartasiewicz.post.vo.PostDeletedEvent;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostFacadeTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentFacade commentFacade;

    @Mock
    private DomainEventPublisher publisher;

    @InjectMocks
    private PostFacade postFacade;

    @Test
    @DisplayName("should correctly prepare and delegate comment creation")
    void shouldCorrectlyPrepareAndDelegateCommentCreation() {
        // given
        int postId = 10;
        var authorDTO = new UserDTO();
        authorDTO.setId(5);
        String commentDescription = "Test comment";

        var postSnapshot = new PostSnapshot(postId, "Test Post", new UserId(1));
        var post = Post.restore(postSnapshot);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        postFacade.createComment(authorDTO, postId, commentDescription);

        // then
        var commentCreatorCaptor = ArgumentCaptor.forClass(CommentCreator.class);
        verify(commentFacade).createComment(commentCreatorCaptor.capture());

        var capturedCommentCreator = commentCreatorCaptor.getValue();
        assertThat(capturedCommentCreator.description()).isEqualTo(commentDescription);
        assertThat(capturedCommentCreator.postId().id()).isEqualTo(postId);
        assertThat(capturedCommentCreator.userId().id()).isEqualTo(authorDTO.getId());
    }

    @Test
    @DisplayName("should publish PostDeletedEvent and delete post by id")
    void shouldPublishPostDeletedEventAndDeletePostById() {
        // given
        int postId = 10;
        var postSnapshot = new PostSnapshot(postId, "Post to delete", new UserId(1));
        var post = Post.restore(postSnapshot);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // when
        postFacade.deletePost(postId);

        // then
        verify(publisher).publish(any(PostDeletedEvent.class));
        verify(postRepository).deleteById(postId);
    }
}
