package com.grzegorzkartasiewicz.post;

import com.grzegorzkartasiewicz.DomainEventPublisher;
import com.grzegorzkartasiewicz.comment.CommentFacade;
import com.grzegorzkartasiewicz.user.UserFacade;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
class PostConfigurator {

    @Bean
    PostFacade postFacade(final PostRepository postRepository, final CommentFacade commentFacade,
                          @Lazy final UserFacade userFacade, final DomainEventPublisher publisher) {
        return new PostFacade(postRepository, commentFacade, userFacade, publisher);
    }
}
