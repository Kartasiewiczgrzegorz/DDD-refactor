package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.login.vo.LoginId;
import com.grzegorzkartasiewicz.post.PostFacade;
import com.grzegorzkartasiewicz.post.vo.PostCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostFacade postFacade;

    @InjectMocks
    private UserFacade userFacade;

    @Test
    @DisplayName("should correctly prepare and delegate post creation")
    void shouldCorrectlyPrepareAndDelegatePostCreation() {
        // given
        int userId = 1;
        String description = "New post from facade test";
        var userSnapshot = new UserSnapshot(userId, "Test", "User", 25);
        var user = User.restore(userSnapshot);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userFacade.createPost(userId, description);

        // then
        var postCreatorCaptor = ArgumentCaptor.forClass(PostCreator.class);
        verify(postFacade).createPost(postCreatorCaptor.capture());

        var capturedPostCreator = postCreatorCaptor.getValue();
        assertThat(capturedPostCreator.description()).isEqualTo(description);
        assertThat(capturedPostCreator.userId().id()).isEqualTo(userId);
    }
}
