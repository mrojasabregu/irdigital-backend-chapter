package org.irdigital.techmeup.api.thirdparty.placeholder;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import org.irdigital.techmeup.domain.exceptions.VTEXAnkaException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.intercorpretail.anka.core.exception.model.AnkaErrorCode;
import pe.intercorpretail.anka.core.exception.model.AnkaException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
@Data
@ConfigurationProperties(prefix = "json-placeholder")
public class JsonPlaceHolderWebClient {
    private WebClient webClient;

    private String url;

    private final List<HttpStatus> handledStatuses = Arrays.asList(
            HttpStatus.NOT_FOUND,
            HttpStatus.FORBIDDEN,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.TOO_MANY_REQUESTS,
            HttpStatus.GATEWAY_TIMEOUT,
            HttpStatus.BAD_REQUEST
    );

    private WebClient getWebClient() {
        if(webClient == null) {
            webClient = WebClient.builder()
                    .baseUrl(url)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }
        return webClient;
    }

    public Flux<JsonNode> findImages() {
        return getWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/photos").build())
                .retrieve()
                .onStatus(this::handleStatus,clientResponse -> {
                    return clientResponse
                            .bodyToMono(String.class)
                            .map(message -> new AnkaException(AnkaErrorCode.SGL_APT_001, "Error getting post from jsonplaceholder: "+message));
                })
                .bodyToFlux(JsonNode.class);
    }

    public Mono<Post> findPost(Long postId) {
        return getWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/posts/{postId}").build(postId))
                .retrieve()
                .onStatus(this::handleStatus,clientResponse -> {
                    return clientResponse
                            .bodyToMono(String.class)
                            .map(message -> new AnkaException(AnkaErrorCode.SGL_APT_001, "Error getting post from jsonplaceholder: "+message));
                })
                .bodyToMono(Post.class);
    }

    public Mono<Comment[]> findComments(Long postId) {
        return getWebClient().get()
                .uri(uriBuilder -> uriBuilder.path("/comments").queryParam("postId", postId).build())
                .retrieve()
                .onStatus(this::handleStatus,clientResponse -> {
                    return clientResponse
                            .bodyToMono(String.class)
                            .map(message -> new AnkaException(AnkaErrorCode.SGL_APT_001, "Error getting comments from jsonplaceholder: "+message));
                })
                .bodyToMono(Comment[].class);
    }

    private boolean handleStatus(HttpStatus httpStatus) {
        return handledStatuses.contains(httpStatus);
    }
}
