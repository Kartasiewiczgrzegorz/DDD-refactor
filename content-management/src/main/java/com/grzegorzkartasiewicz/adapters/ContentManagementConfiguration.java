package com.grzegorzkartasiewicz.adapters;

import com.grzegorzkartasiewicz.app.PostService;
import com.grzegorzkartasiewicz.domain.DomainEventPublisher;
import com.grzegorzkartasiewicz.domain.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
class ContentManagementConfiguration {

  @Bean
  PostService postService(PostRepository postRepository, DomainEventPublisher domainEventPublisher) {
    return new PostService(postRepository, domainEventPublisher);
  }
}
