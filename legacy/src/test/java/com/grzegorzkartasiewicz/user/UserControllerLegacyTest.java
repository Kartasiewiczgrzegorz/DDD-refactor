package com.grzegorzkartasiewicz.user;

import com.grzegorzkartasiewicz.post.PostDTO;
import com.grzegorzkartasiewicz.post.PostFacade;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(UserControllerLegacy.class)
class UserControllerLegacyTest {

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

        BDDMockito.given(userFacade.getUser(ArgumentMatchers.any(UserId.class))).willReturn(userDto);
        BDDMockito.given(postFacade.getPostsForUser(userId)).willReturn(List.of(postDto));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/legacy/user").param("id", String.valueOf(userId)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user"))
                .andExpect(MockMvcResultMatchers.model().attribute("userProfile", userDto))
                .andExpect(MockMvcResultMatchers.model().attribute("postsForUser", List.of(postDto)));
    }

    @Test
    @DisplayName("should redirect after creating post for logged in user")
    void shouldCreatePostForLoggedInUser() throws Exception {
        // given
        var loggedUser = new UserDTO();
        loggedUser.setId(1);
        String description = "New post from a controller test";

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.post("/legacy/user/posts")
                        .param("description", description)
                        .sessionAttr("user", loggedUser))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/user?id=" + loggedUser.getId()));

        // verify
        Mockito.verify(userFacade).createPost(ArgumentMatchers.eq(loggedUser.getId()), ArgumentMatchers.eq(description));
    }
}
