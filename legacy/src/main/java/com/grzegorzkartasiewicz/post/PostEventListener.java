package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.comment.CommentFacade;
import com.grzegorzkartasiewicz.post.vo.PostDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class PostEventListener {
    private static final Logger logger = LoggerFactory.getLogger(PostEventListener.class);
    private final CommentFacade commentFacade;

    PostEventListener(CommentFacade commentFacade) {
        this.commentFacade = commentFacade;
    }

    @EventListener
    public void handle(PostDeletedEvent event) {
        logger.info("Received PostDeletedEvent for postId: {}. Deleting related comments.", event.getPostId().id());
        commentFacade.deleteCommentsForPost(event.getPostId());
    }
}
