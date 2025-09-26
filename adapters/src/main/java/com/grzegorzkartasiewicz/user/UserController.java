package com.grzegorzkartasiewicz.user;


import com.grzegorzkartasiewicz.comment.vo.CommentId;
import com.grzegorzkartasiewicz.post.PostFacade;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/user")
class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    public static final String MODEL_ATTRIBUTE_USER = "user";
    private final UserFacade userFacade;
    private final PostFacade postFacade;

    UserController(UserFacade userFacade, PostFacade postFacade) {
        this.userFacade = userFacade;
        this.postFacade = postFacade;
    }


    @GetMapping
    public String showUserProfile(@RequestParam("id") int userId, Model model) {
        model.addAttribute("userProfile", userFacade.getUser(new com.grzegorzkartasiewicz.user.vo.UserId(userId)));
        // Używamy nowej metody fasady do pobrania "bogatych" DTO
        model.addAttribute("postsForUser", postFacade.getPostsForUser(userId));
        return "user";
    }
    @PostMapping("/posts")
    String addUserPost(HttpSession session, String description){
        logger.info("Creating new post!");
        var loggedUser = (UserDTO) session.getAttribute("user");
        if (loggedUser != null) {
            userFacade.createPost(loggedUser.getId(), description);
            return "redirect:/user?id=" + loggedUser.getId();
        }
        return "redirect:/";
    }

    @PostMapping("/comments/{postId}")
    String addUserComment(HttpSession session,
            @ModelAttribute("users") UserDTO current, Model model,
                          @PathVariable int postId,
                          String description){
        logger.info("Creating new comment!");
        var loggedUser= (UserDTO) session.getAttribute(MODEL_ATTRIBUTE_USER);
        userFacade.createComment(loggedUser,postId,description);
        model.addAttribute(MODEL_ATTRIBUTE_USER, current);
        return MODEL_ATTRIBUTE_USER;
    }

    @PostMapping("/posts/delete/{postId}")
    String deleteUserPost(@ModelAttribute("users") UserDTO current, Model model,
                          @PathVariable int postId){
        logger.info("Deleting post!");
        userFacade.deletePost(postId);
        model.addAttribute(MODEL_ATTRIBUTE_USER, current);
        return MODEL_ATTRIBUTE_USER;
    }

    @PostMapping("/comment/delete/{commentId}")
    String deleteUserComment(@ModelAttribute("users") UserDTO current, Model model,
                          @PathVariable int commentId){
        logger.info("Deleting comment!");
        userFacade.deleteComment(new CommentId(commentId));
        model.addAttribute(MODEL_ATTRIBUTE_USER, current);
        return MODEL_ATTRIBUTE_USER;
    }
    @GetMapping("/home")
    public String showCurrentUserProfile(HttpSession session) {
        UserDTO loggedUser = (UserDTO) session.getAttribute("user");
        if (loggedUser != null) {
            return "redirect:/user?id=" + loggedUser.getId();
        }
        return "redirect:/";
    }
}
