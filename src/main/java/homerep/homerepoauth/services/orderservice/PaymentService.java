package homerep.homerepoauth.services.orderservice;

import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.PaymentType;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class PaymentService {
    private final WebClient webClient;
    private final String ORDER_SERVICE_URL;

    public PaymentService(WebClient webClient, HomeRepProperties props) {
        this.webClient = webClient;
        this.ORDER_SERVICE_URL = props.getOrderservice() + "/payments";
    }

    public Mono<DefaultResponse> activatePayment(String paymentName, String authHeader) {
        return webClient.patch()
                .uri(ORDER_SERVICE_URL + "/activate?paymentName={name}", paymentName)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Order Service error: " + error)))
                )
                .bodyToMono(DefaultResponse.class);
    }

    public Mono<DefaultResponse> deactivatePayment(String paymentName, String authHeader) {
        return webClient.patch()
                .uri(ORDER_SERVICE_URL + "/deactivate?paymentName={name}", paymentName)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new RuntimeException("Order Service error: " + error)))
                )
                .bodyToMono(DefaultResponse.class);
    }
}