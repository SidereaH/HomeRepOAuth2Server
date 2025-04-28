package homerep.homerepoauth.services.orderservice;

import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.PaymentType;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
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

    public Mono<Object> activatePayment(String paymentName) {
        return webClient.patch()
                .uri(ORDER_SERVICE_URL+ "/activate?paymentName={name}", paymentName)
                .retrieve()

                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error from Order Service: " + errorBody)))
                )
                .bodyToMono(Object.class);
    }
    public Mono<Object> deactivatePayment(String paymentName) {
        return webClient.patch()
                .uri(ORDER_SERVICE_URL+ "/deactivate?paymentName={name}", paymentName)

                .retrieve()

                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error from Order Service: " + errorBody)))
                )

                .bodyToMono(Object.class);
    }
}