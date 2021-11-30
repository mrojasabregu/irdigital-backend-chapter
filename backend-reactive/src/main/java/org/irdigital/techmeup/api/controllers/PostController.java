package org.irdigital.techmeup.api.controllers;

import lombok.RequiredArgsConstructor;
import org.irdigital.techmeup.api.dto.PostDTO;
import org.irdigital.techmeup.api.services.PostsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class PostController {
    private final PostsService postsService;

    @GetMapping("/posts/{postId}")
    public Mono<PostDTO> findPostInfo(@PathVariable("postId") Long postId) {
        return postsService.findPostInfo(postId);
    }

    @GetMapping("/posts/images")
    public Mono<List<String>> findImages() {
        return postsService.findImages();
    }
}
