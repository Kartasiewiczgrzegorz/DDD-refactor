package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.PostService;
import com.grzegorzkartasiewicz.domain.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ContentManagementConfiguration {

  @Bean
  PostService postService(PostRepository postRepository) {
    return new PostService(postRepository);
  }
}
