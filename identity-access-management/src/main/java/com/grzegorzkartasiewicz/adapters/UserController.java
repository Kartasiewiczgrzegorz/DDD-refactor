package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.LoggedUser;
import com.grzegorzkartasiewicz.app.RegisteredUser;
import com.grzegorzkartasiewicz.app.ResetPasswordRequest;
import com.grzegorzkartasiewicz.app.UserLogInRequest;
import com.grzegorzkartasiewicz.app.UserRegistrationRequest;
import com.grzegorzkartasiewicz.app.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
class UserController {

  private final UserService userService;

  @Operation(summary = "Register a new user",
      description = "Creates a new user account based on the provided data. Returns the created user's information along with a JWT token.",
      responses = {
          @ApiResponse(responseCode = "201", description = "User created successfully",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisteredUser.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., weak password, invalid email format)"),
          @ApiResponse(responseCode = "409", description = "User with the given email already exists")
      })
  @PostMapping
  ResponseEntity<RegisteredUser> registerUser(@RequestBody UserRegistrationRequest signInRequest) {
    RegisteredUser response = userService.signIn(signInRequest);
    return ResponseEntity.created(URI.create("/users/" + response.id())).body(response);
  }

  @Operation(summary = "Log in a user",
      description = "Authenticates a user and returns their information along with a new JWT token.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Login successful",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoggedUser.class))),
          @ApiResponse(responseCode = "401", description = "Invalid credentials (wrong email or password)"),
          @ApiResponse(responseCode = "403", description = "User account is blocked")
      })
  @PostMapping("/login")
  ResponseEntity<LoggedUser> logIn(@RequestBody UserLogInRequest userLogInRequest) {
    return ResponseEntity.ok(userService.logIn(userLogInRequest));
  }

  @Operation(summary = "Request a password reset",
      description = "Initiates the password reset process for a given email. A successful response does not guarantee a user with that email exists, for security reasons.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Password reset process initiated successfully."),
          @ApiResponse(responseCode = "400", description = "Invalid email format.")
      })
  @PostMapping("/reset-password")
  ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
    userService.requestResetPassword(resetPasswordRequest);
    return ResponseEntity.ok().build();
  }
}
