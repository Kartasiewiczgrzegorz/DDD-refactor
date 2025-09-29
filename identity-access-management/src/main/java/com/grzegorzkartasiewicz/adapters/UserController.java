package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.LoggedUser;
import com.grzegorzkartasiewicz.app.RegisteredUser;
import com.grzegorzkartasiewicz.app.UserLogInRequest;
import com.grzegorzkartasiewicz.app.UserRegistrationRequest;
import com.grzegorzkartasiewicz.app.UserService;
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
class UserController {

  private final UserService userService;

  @PostMapping
  ResponseEntity<RegisteredUser> registerUser(@RequestBody UserRegistrationRequest signInRequest) {
    RegisteredUser response = userService.signIn(signInRequest);
    return ResponseEntity.created(URI.create("/users/" + response.id())).body(response);
  }

  @PostMapping("/login")
  ResponseEntity<LoggedUser> logIn(@RequestBody UserLogInRequest userLogInRequest) {
    return ResponseEntity.ok(userService.logIn(userLogInRequest));
  }
}
