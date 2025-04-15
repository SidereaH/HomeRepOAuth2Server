package homerep.homerepoauth.services.orderservice;

import homerep.homerepoauth.models.orderservice.PaymentType;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {
    private final WebClient webClient;

    public PaymentService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<DefaultResponse> activatePayment(String paymentName) {
        return webClient.patch()
                .uri("http://localhost:8084/payments/activate?paymentName={name}", paymentName)
                .retrieve()
                .bodyToMono(DefaultResponse.class);
    }
    public Mono<DefaultResponse> deactivatePayment(String paymentName) {
        return webClient.patch()
                .uri("http://localhost:8084/payments/deactivate?paymentName={name}", paymentName)
                .retrieve()
                .bodyToMono(DefaultResponse.class);
    }
}