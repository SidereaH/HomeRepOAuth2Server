package homerep.homerepoauth.controllers.orderservice;

import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.PaymentType;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import homerep.homerepoauth.services.orderservice.PaymentService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class GatewayPaymentController {

    private final RestTemplate restTemplate;
    private final String ORDER_SERVICE_URL;
    private final PaymentService paymentService;

    public GatewayPaymentController(RestTemplate restTemplate,
                                    HomeRepProperties props,
                                    PaymentService paymentService) {
        this.restTemplate = restTemplate;
        this.ORDER_SERVICE_URL = props.getOrderservice() + "/payments";
        this.paymentService = paymentService;
    }
    @GetMapping
    public ResponseEntity<List<PaymentType>> getAllPayments() {
        ParameterizedTypeReference<List<PaymentType>> responseType =
                new ParameterizedTypeReference<List<PaymentType>>() {};

        ResponseEntity<List<PaymentType>> response = restTemplate.exchange(
                ORDER_SERVICE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        // Убираем ручной расчет Content-Length
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }

    @PostMapping
    public ResponseEntity<DefaultResponse> createPayment(@RequestBody PaymentType paymentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "close");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PaymentType> request = new HttpEntity<>(paymentType, headers);

        try {
            ResponseEntity<DefaultResponse> response = restTemplate.exchange(
                    ORDER_SERVICE_URL,
                    HttpMethod.POST,
                    request,
                    DefaultResponse.class
            );

            // Явно устанавливаем Content-Length для Nginx
            return ResponseEntity.status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            DefaultResponse errorResponse = new DefaultResponse("Error", e.getMessage());
            return ResponseEntity.badRequest()
                    .header("Content-Length",
                            String.valueOf(errorResponse.toString().getBytes().length))
                    .body(errorResponse);
        }
    }

    @PatchMapping("/activate")
    public Mono<ResponseEntity<DefaultResponse>> activatePayment(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam String paymentName) {
        return paymentService.activatePayment(paymentName, authHeader)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest()
                                .body(new DefaultResponse(null, "Error: " + e.getMessage()))
                ));
    }
    @PatchMapping("/deactivate")
    public Mono<ResponseEntity<DefaultResponse>> deactivatePayment(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam String paymentName) {
        return paymentService.deactivatePayment(paymentName, authHeader)
                .map(response -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(response))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.badRequest()
                                .body(new DefaultResponse(null, "Error: " + e.getMessage()))
                ));
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePayment(@RequestParam String paymentTypeName) {
        String url = ORDER_SERVICE_URL + "?paymentTypeName=" + paymentTypeName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Connection", "close");

        restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );

        return ResponseEntity.noContent()
                .build();
    }
}