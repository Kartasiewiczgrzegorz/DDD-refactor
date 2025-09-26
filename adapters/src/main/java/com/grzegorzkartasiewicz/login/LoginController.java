package com.grzegorzkartasiewicz.login;
/*
 * Spring controller made for logging and signing up users.
 *
 * @author Grzegorz Kartasiewicz
 *
 */

import com.grzegorzkartasiewicz.user.UserDTO;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/")
class LoginController implements ErrorController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    public static final String MODEL_ATTRIBUTE_LOGIN = "login";
    public static final String SESSION_ATTRIBUTE_USER = "user";
    private final LoginFacade loginFacade;

    LoginController(LoginFacade loginFacade) {
        this.loginFacade = loginFacade;
    }

    @GetMapping("/")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new LoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        logger.info("Attempting to log in user: {}", username);
        UserDTO loggedUser = loginFacade.logInUser(username, password);
        if (loggedUser != null) {
            session.setAttribute("user", loggedUser);
            return "redirect:/posts/home";
        }
        model.addAttribute("message", "Invalid login or password");
        model.addAttribute("login", new LoginDTO());
        return "login";
    }

    @PostMapping("/register")
    public String handleRegistration(@ModelAttribute("login") LoginDTO loginDTO, HttpSession session) {
        logger.info("Registering new user: {}", loginDTO.getNick());
        UserDTO registeredUser = loginFacade.signInUser(loginDTO);
        session.setAttribute("user", registeredUser);
        return "redirect:/posts/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
