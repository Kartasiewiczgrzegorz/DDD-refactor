package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.comment.vo.CommentId;
import com.grzegorzkartasiewicz.user.UserDTO;
import com.grzegorzkartasiewicz.user.UserFacade;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    public static final String MODEL_ATTRIBUTE_POSTS = "posts";
    public static final String MODEL_ATTRIBUTE_POST = "post";
    private final PostRepository repository;
    private final PostFacade postFacade;
    private final UserFacade userFacade;

    PostController(PostRepository repository, PostFacade postFacade, UserFacade userFacade) {
        this.repository = repository;
        this.postFacade = postFacade;
        this.userFacade = userFacade;
    }

    @GetMapping("/home")
    public String showHomePage(Model model) {
        logger.info("Showing home page!");
        // Używamy nowej metody fasady do pobrania "bogatych" DTO
        model.addAttribute("posts", postFacade.getAllPostsForHomePage());
        return "posts";
    }
    @GetMapping
    String showPosts(HttpSession session,Model model){
        logger.info("Showing home page for logged user!");
        var postToEdit = new PostDTO();
        session.setAttribute(MODEL_ATTRIBUTE_POSTS, getPosts());
        model.addAttribute(MODEL_ATTRIBUTE_POST, postToEdit);
        return MODEL_ATTRIBUTE_POSTS;

    }
    @PostMapping
    public String createPost(@RequestParam String description, HttpSession session) {
        UserDTO loggedUser = (UserDTO) session.getAttribute("user");
        if (loggedUser != null) {
            userFacade.createPost(loggedUser.getId(), description);
        }
        return "redirect:/posts/home";
    }

    @PostMapping("/comments/{postId}")
    String addUserPostComment(
                        HttpSession session,
                       Model model,
                       @PathVariable int postId,
                       String description){
        logger.info("Creating new comment!");
        UserDTO user = (UserDTO) session.getAttribute("user");
        postFacade.createComment(user, postId, description);
        model.addAttribute(MODEL_ATTRIBUTE_POSTS, getPosts());
        return MODEL_ATTRIBUTE_POSTS;
    }
    @GetMapping("/search")
    String searchUsersAndPosts(Model model, @RequestParam String search){
        logger.info("Showing searched users and posts!");
        model.addAttribute(MODEL_ATTRIBUTE_POSTS, postFacade.searchPosts(search));
        model.addAttribute("users", userFacade.searchUsers(search));
        return MODEL_ATTRIBUTE_POSTS;
    }
    @PostMapping("/comment/delete/{commentId}")
    String deleteComment(HttpSession session,
                         Model model,
                         @PathVariable int commentId){
        logger.info("Deleting comment!");
        postFacade.deleteComment(new CommentId(commentId));
        model.addAttribute(MODEL_ATTRIBUTE_POSTS, getPosts());
        return MODEL_ATTRIBUTE_POSTS;
    }
    @PostMapping("/delete/{postId}")
    String deletePost(HttpSession session,
                         Model model,
                         @PathVariable int postId){
        logger.info("Deleting post!");
        postFacade.deletePost(postId);
        model.addAttribute(MODEL_ATTRIBUTE_POSTS, getPosts());
        return MODEL_ATTRIBUTE_POSTS;
    }

    @ModelAttribute("posts")
    List<PostDTO> getPosts(){
        return repository.findAll().stream().map(postFacade::assemblePostDTO).toList();
    }
}
