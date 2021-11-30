package org.irdigital.techmeup.api.thirdparty.vtex;

import lombok.Data;
import org.irdigital.techmeup.domain.exceptions.VTEXAnkaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Data
public class VTEXWebClient {

    private final WebClient webClient;
    private final String tenantId;
    private final List<HttpStatus> handledStatuses = Arrays.asList(
            HttpStatus.NOT_FOUND,
            HttpStatus.FORBIDDEN,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.TOO_MANY_REQUESTS,
            HttpStatus.GATEWAY_TIMEOUT,
            HttpStatus.BAD_REQUEST
    );

    public VTEXWebClient(String tenantId, String baseURL, String appId, String appSecret) {
        this.tenantId = tenantId;
        webClient = WebClient.builder()
                .baseUrl(baseURL)
                .defaultHeader("X-VTEX-API-AppToken", appSecret)
                .defaultHeader("X-VTEX-API-AppKey", appId)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Mono<VTEXOrder> findOrderById(String orderId) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/oms/pvt/orders/{orderId}").build(orderId))
                .retrieve()
                .onStatus(this::handleStatus,clientResponse -> {
                    return clientResponse
                            .bodyToMono(String.class)
                            .map(message -> new VTEXAnkaException(tenantId, orderId, "HTTP status error from vtex with response " + message));
                })
                .bodyToMono(VTEXOrder.class)
                .timeout(Duration.ofMillis(5000), Mono.error(new VTEXAnkaException(tenantId, orderId, "Timeout Exception handling VTEX order " + orderId)))
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)).filter(this::isError));
    }


    private boolean isError(Throwable throwable) {
        return throwable instanceof WebClientResponseException && ((WebClientResponseException) throwable).getStatusCode().isError();
    }

    private boolean handleStatus(HttpStatus httpStatus) {
        return handledStatuses.contains(httpStatus);
    }
}
