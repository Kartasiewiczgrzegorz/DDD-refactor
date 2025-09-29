package com.grzegorzkartasiewicz.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.grzegorzkartasiewicz.adapters.UserController;
import com.grzegorzkartasiewicz.app.LoggedUser;
import com.grzegorzkartasiewicz.app.RegisteredUser;
import com.grzegorzkartasiewicz.app.UserLogInRequest;
import com.grzegorzkartasiewicz.app.UserRegistrationRequest;
import com.grzegorzkartasiewicz.app.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grzegorzkartasiewicz.domain.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Test
  @DisplayName("should register user and return 201 Created")
  void registerUser() throws Exception {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe",
        "john.doe@example.com", "Password123!");
    RegisteredUser response = new RegisteredUser(1L, "John", "Doe", "john.doe@example.com",
        "dummy-token");

    when(userService.signIn(any(UserRegistrationRequest.class))).thenReturn(response);

    // when & then
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "/users/1"))
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.token").value("dummy-token"));
  }

  @Test
  @DisplayName("should return 400 Bad Request when registration data is invalid")
  void registerUser_whenInvalidRequest_shouldReturnBadRequest() throws Exception {
    // given
    // Request with invalid email and password
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe", "invalid-email",
        "short");
    when(userService.signIn(any(UserRegistrationRequest.class))).thenThrow(
        new IllegalArgumentException("Invalid data"));

    // when & then
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }


  @Test
  @DisplayName("should log in user and return 200 OK")
  void logIn() throws Exception {
    // given
    UserLogInRequest request = new UserLogInRequest(new Email("john.doe@example.com"),
        "Password123!");
    LoggedUser response = new LoggedUser("John", "Doe", "john.doe@example.com", "dummy-token");

    when(userService.logIn(any(UserLogInRequest.class))).thenReturn(response);

    // when & then
    mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.token").value("dummy-token"));
  }

  @Test
  @DisplayName("should return 401 Unauthorized when login credentials are incorrect")
  void logIn_whenCredentialsIncorrect_shouldReturnUnauthorized() throws Exception {
    // given
    UserLogInRequest request = new UserLogInRequest(new Email("john.doe@example.com"),
        "wrongPassword");
    when(userService.logIn(any(UserLogInRequest.class))).thenThrow(
        new IllegalArgumentException("Passwords do not match"));

    // when & then
    mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("should return 403 Forbidden when user is blocked")
  void logIn_whenUserIsBlocked_shouldReturnForbidden() throws Exception {
    // given
    UserLogInRequest request = new UserLogInRequest(new Email("blocked.user@example.com"),
        "Password123!");
    when(userService.logIn(any(UserLogInRequest.class))).thenThrow(
        new IllegalArgumentException("User is blocked"));

    // when & then
    mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden());
  }
}