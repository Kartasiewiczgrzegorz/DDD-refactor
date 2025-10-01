package com.grzegorzkartasiewicz.adapters;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.grzegorzkartasiewicz.app.UserLogInRequest;
import com.grzegorzkartasiewicz.app.UserRegistrationRequest;
import com.grzegorzkartasiewicz.app.UserService;
import com.grzegorzkartasiewicz.domain.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Zapewnia, że każda metoda testowa działa w transakcji, która jest wycofywana po jej zakończeniu.
class UserControllerIT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserService userService; // Wstrzyknięcie prawdziwego serwisu

  @Test
  @DisplayName("should register user and return 201 Created for valid data")
  void registerUser_happyPath() throws Exception {
    // given
    UserRegistrationRequest request = new UserRegistrationRequest("John", "Doe",
        "john.doe@example.com", "Password123!");

    // when & then
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        // ID jest generowane, więc sprawdzamy jego obecność i typ
        .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/users/")))
        .andExpect(jsonPath("$.id").isNumber())
        .andExpect(jsonPath("$.firstName").value("John"))
        .andExpect(jsonPath("$.email").value("john.doe@example.com"))
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  @DisplayName("should return 400 Bad Request when registration data is invalid (e.g., weak password)")
  void registerUser_whenInvalidPassword_shouldReturnBadRequest() throws Exception {
    // given
    // Żądanie z hasłem, które nie spełnia kryteriów
    UserRegistrationRequest request = new UserRegistrationRequest("Jane", "Doe",
        "jane.doe@example.com", "weak");

    // when & then
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should log in successfully and return 200 OK with token")
  void logIn_happyPath() throws Exception {
    // given: najpierw zarejestruj użytkownika
    UserRegistrationRequest registrationRequest = new UserRegistrationRequest("Peter", "Jones",
        "peter.jones@example.com", "Password123!");
    userService.signIn(registrationRequest);

    // teraz przygotuj żądanie logowania
    UserLogInRequest loginRequest = new UserLogInRequest(new Email("peter.jones@example.com"),
        "Password123!");

    // when & then
    mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName").value("Peter"))
        .andExpect(jsonPath("$.email").value("peter.jones@example.com"))
        .andExpect(jsonPath("$.token").isString());
  }

  @Test
  @DisplayName("should return 400 Bad Request when login with incorrect password")
  void logIn_whenPasswordIsIncorrect_shouldReturnBadRequest() throws Exception {
    // given: najpierw zarejestruj użytkownika
    UserRegistrationRequest registrationRequest = new UserRegistrationRequest("Alice", "Smith",
        "alice.smith@example.com", "Password123!");
    userService.signIn(registrationRequest);

    // teraz przygotuj żądanie logowania z błędnym hasłem
    UserLogInRequest loginRequest = new UserLogInRequest(new Email("alice.smith@example.com"),
        "WrongPassword123!");

    // when & then
    mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("should block user after 6 failed login attempts and return 400 on subsequent attempts")
  void logIn_shouldBlockUserAfterMultipleFailures() throws Exception {
    // given: zarejestruj użytkownika
    String email = "blocked.user@example.com";
    String correctPassword = "Password123!";
    String wrongPassword = "WrongPassword123!";
    UserRegistrationRequest registrationRequest = new UserRegistrationRequest("Blocked", "User",
        email, correctPassword);
    userService.signIn(registrationRequest);

    UserLogInRequest wrongPasswordRequest = new UserLogInRequest(new Email(email), wrongPassword);
    UserLogInRequest correctPasswordRequest = new UserLogInRequest(new Email(email),
        correctPassword);

    // when: wykonaj 6 nieudanych prób logowania
    for (int i = 0; i < 6; i++) {
      mockMvc.perform(post("/users/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
          .andExpect(
              status().isBadRequest()); // Oczekuj 400 Bad Request przy każdej nieudanej próbie
    }

    // then: siódma próba (nawet z poprawnym hasłem) powinna zakończyć się niepowodzeniem, ponieważ użytkownik jest zablokowany
    mockMvc.perform(post("/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(correctPasswordRequest)))
        .andExpect(status().isBadRequest());
  }
}