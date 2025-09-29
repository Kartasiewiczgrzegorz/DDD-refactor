package adapters;

import app.LoggedUser;
import app.RegisteredUser;
import app.UserLogInRequest;
import app.UserRegistrationRequest;
import app.UserService;
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
