package com.grzegorzkartasiewicz.login;

import com.grzegorzkartasiewicz.user.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginFacade loginFacade;

    @MockBean
    private LoginRepository loginRepository;

    @Test
    @DisplayName("should log in user and redirect to posts page on correct credentials")
    void shouldLogInUserAndRedirect() throws Exception {
        // given
        String username = "testUser";
        String password = "correctPassword";

        var loggedUserDto = new UserDTO();
        loggedUserDto.setId(1);
        loggedUserDto.setName("Test");
        loggedUserDto.setSurname("User");

        given(loginFacade.logInUser(username, password)).willReturn(loggedUserDto);

        // when & then
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/posts/home"))
                .andExpect(request().sessionAttribute("user", loggedUserDto));
    }

    @Test
    @DisplayName("should show login page with error message on incorrect credentials")
    void shouldShowLoginPageWithErrorOnIncorrectCredentials() throws Exception {
        // given
        String username = "testUser";
        String password = "wrongPassword";
        given(loginFacade.logInUser(username, password)).willReturn(null);

        // when & then
        mockMvc.perform(post("/login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("message", "Invalid login or password"));
    }
}
