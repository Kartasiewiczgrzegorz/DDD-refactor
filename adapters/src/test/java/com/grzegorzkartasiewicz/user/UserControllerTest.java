package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.post.PostDTO;
import com.grzegorzkartasiewicz.post.PostFacade;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserFacade userFacade;

    @MockBean
    private PostFacade postFacade;

    @Test
    @DisplayName("should show user profile with their posts when user exists")
    void shouldShowUserProfileWhenUserExists() throws Exception {
        // given
        int userId = 1;
        var userDto = new UserDTO();
        userDto.setId(userId);
        userDto.setName("Test");

        var postDto = new PostDTO();
        postDto.setDescription("User's post");

        given(userFacade.getUser(any(UserId.class))).willReturn(userDto);
        given(postFacade.getPostsForUser(userId)).willReturn(List.of(postDto));

        // when & then
        mockMvc.perform(get("/user").param("id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(view().name("user"))
                .andExpect(model().attribute("userProfile", userDto))
                .andExpect(model().attribute("postsForUser", List.of(postDto)));
    }

    @Test
    @DisplayName("should redirect after creating post for logged in user")
    void shouldCreatePostForLoggedInUser() throws Exception {
        // given
        var loggedUser = new UserDTO();
        loggedUser.setId(1);
        String description = "New post from a controller test";

        // when & then
        mockMvc.perform(post("/user/posts")
                        .param("description", description)
                        .sessionAttr("user", loggedUser))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user?id=" + loggedUser.getId()));

        // verify
        verify(userFacade).createPost(eq(loggedUser.getId()), eq(description));
    }
}
