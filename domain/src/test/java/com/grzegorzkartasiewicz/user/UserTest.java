package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.login.vo.LoginId;
import com.grzegorzkartasiewicz.post.vo.PostCreator;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("should prepare post creator with correct data")
    void shouldPreparePostCreatorWithCorrectData() {
        // given
        var userSnapshot = new UserSnapshot(1, "John", "Doe", 30);
        var user = User.restore(userSnapshot);
        var description = "This is a new post.";

        // when
        PostCreator postCreator = user.prepareNewPost(description);

        // then
        assertThat(postCreator).isNotNull();
        assertThat(postCreator.description()).isEqualTo(description);
        assertThat(postCreator.userId()).isEqualTo(new UserId(1));
    }
}