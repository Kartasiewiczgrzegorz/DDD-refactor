package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.comment.CommentDTO;
import com.grzegorzkartasiewicz.post.vo.PostId;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.UserFacade;
import com.grzegorzkartasiewicz.user.vo.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostFacade postFacade;

    @MockBean
    private UserFacade userFacade;

    @MockBean
    private PostRepository postRepository;

    @Test
    @DisplayName("should return home page with all posts")
    void shouldReturnHomePageWithAllPosts() throws Exception {
        // given
        PostDTO postDto = new PostDTO();
        postDto.setId(1);
        postDto.setDescription("A sample post");
        postDto.setUserId(new UserId(1));
        postDto.setAuthorName("Test");
        postDto.setAuthorSurname("User");
        postDto.setComments(Collections.emptyList());

        given(postFacade.getAllPostsForHomePage()).willReturn(List.of(postDto));

        // when & then
        mockMvc.perform(get("/posts/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("posts", List.of(postDto)))
                .andExpect(model().attributeDoesNotExist("post"));
    }

    @Test
    @DisplayName("should return posts and users for a given search query")
    void shouldReturnPostsAndUsersForSearchQuery() throws Exception {
        // given
        String query = "test";
        PostDTO postDto = new PostDTO();
        postDto.setId(1);
        postDto.setDescription("This is a test post.");
        postDto.setUserId(new UserId(2));

        UserDTO userDto = new UserDTO();
        userDto.setId(2);
        userDto.setName("Test User");

        given(postFacade.searchPosts(query)).willReturn(List.of(postDto));
        given(userFacade.searchUsers(query)).willReturn(List.of(userDto));

        // when & then
        mockMvc.perform(get("/posts/search").param("search", query))
                .andExpect(status().isOk())
                .andExpect(view().name("posts"))
                .andExpect(model().attribute("posts", List.of(postDto)))
                .andExpect(model().attribute("users", List.of(userDto)));
    }
}
