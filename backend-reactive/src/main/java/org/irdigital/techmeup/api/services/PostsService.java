package org.irdigital.techmeup.api.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import org.irdigital.techmeup.api.dto.CommentDTO;
import org.irdigital.techmeup.api.dto.PostDTO;
import org.irdigital.techmeup.api.thirdparty.placeholder.Comment;
import org.irdigital.techmeup.api.thirdparty.placeholder.JsonPlaceHolderWebClient;
import org.irdigital.techmeup.api.thirdparty.placeholder.Post;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsService {
    private final JsonPlaceHolderWebClient webClient;

    public Mono<List<String>> findImages() {
       return webClient.findImages()
                .map(this::extractImages).collectList();
    }

    private String extractImages(JsonNode jsonNode) {
        return JsonPath.parse(jsonNode.toString()).read("$.title");
    }

    public Mono<PostDTO> findPostInfo(Long postId) {
        //TUPLE<T1, T2>
        return webClient.findPost(postId)
                        .zipWith(webClient.findComments(postId))
                        .map(this::assembleResponse);
    }

    private PostDTO assembleResponse(Tuple2<Post, Comment[]> objects) {
        Post post = objects.getT1();
        List<CommentDTO> commentList = Arrays.stream(objects.getT2()).map(this::createCommentDTO).collect(Collectors.toList());
        return new PostDTO(post.getId(), post.getTitle(), post.getBody(), commentList);
    }

    private CommentDTO createCommentDTO(Comment comment) {
        return new CommentDTO(comment.getEmail(), comment.getName(), comment.getBody());
    }
}
