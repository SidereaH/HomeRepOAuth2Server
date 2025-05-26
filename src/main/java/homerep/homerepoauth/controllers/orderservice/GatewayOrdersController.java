package homerep.homerepoauth.controllers.orderservice;

import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.Order;
import homerep.homerepoauth.models.orderservice.dto.AssignResponse;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/orders")
public class GatewayOrdersController {

    private final RestTemplate restTemplate;
    private final String ORDER_SERVICE_URL;

    public GatewayOrdersController(RestTemplate restTemplate, HomeRepProperties props) {
        this.restTemplate = restTemplate;
        this.ORDER_SERVICE_URL = props.getOrderservice() + "/orders";
    }

    @PostMapping
    public ResponseEntity<DefaultResponse> createOrder(@RequestBody Order order) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        try {
            ResponseEntity<DefaultResponse> response = restTemplate.exchange(
                    ORDER_SERVICE_URL,
                    HttpMethod.POST,
                    request,
                    DefaultResponse.class
            );
            return ResponseEntity
                    .status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new DefaultResponse(order, e.getMessage()));
        }
    }
        @GetMapping("/user/{clientId}")
        public ResponseEntity<List<Object>> getUserOrders(@PathVariable Long clientId) {
            String url = ORDER_SERVICE_URL + "/user/" + clientId;

            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        }
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        ParameterizedTypeReference<List<Order>> responseType =
                new ParameterizedTypeReference<List<Order>>() {};

        ResponseEntity<List<Order>> response = restTemplate.exchange(
                ORDER_SERVICE_URL,
                HttpMethod.GET,
                null,
                responseType
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }
    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        String url = ORDER_SERVICE_URL + "/order/" + id;
        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                Order.class
        );
    }

    @PutMapping
    public ResponseEntity<DefaultResponse<Order, String>> updateOrder(@RequestBody Order order) {
        String url = ORDER_SERVICE_URL;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Order> request = new HttpEntity<>(order, headers);

        return restTemplate.exchange(
                url,
                HttpMethod.PUT,
                request,
                new ParameterizedTypeReference<DefaultResponse<Order, String>>() {}
        );
    }
    @DeleteMapping("/{orderId")
    public ResponseEntity<Object> deleteOrder(@PathVariable Long orderId) {
        String url = ORDER_SERVICE_URL + "/" + orderId;

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    String.class
            );

            return ResponseEntity.status(response.getStatusCode()).build();
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();
        } catch (RestClientException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/order/findWorker")
    public ResponseEntity<Integer> findWorker(@RequestParam String orderID) {
        String url = ORDER_SERVICE_URL + "/order/findWorker?orderID=" + orderID;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Integer> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    Integer.class
            );
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(-1);
        }
    }

    @PostMapping("/order/{orderId}/assignWorker/{workerId}")
    public ResponseEntity<AssignResponse> assignWorker(
            @PathVariable String orderId,
            @PathVariable String workerId) {
        String url = ORDER_SERVICE_URL + "/order/" + orderId + "/assignWorker/" + workerId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<AssignResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(headers),
                    AssignResponse.class
            );
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AssignResponse( e.getMessage(),0L,0L));
        }
    }
}