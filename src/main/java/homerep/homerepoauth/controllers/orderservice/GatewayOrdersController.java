package homerep.homerepoauth.controllers.orderservice;


import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.Order;
import homerep.homerepoauth.models.orderservice.dto.AssignResponse;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class GatewayOrdersController {

    private final RestTemplate restTemplate;

    private final String ORDER_SERVICE_URL;


    public GatewayOrdersController(RestTemplate restTemplate, HomeRepProperties props) {
        this.restTemplate = restTemplate;
        this.ORDER_SERVICE_URL  = props.getOrderservice() + "/orders";
    }

    @PostMapping
    public ResponseEntity<DefaultResponse> createOrder(@RequestBody Order order) {
        try{
            restTemplate.postForEntity(ORDER_SERVICE_URL, order, DefaultResponse.class);
        }
        catch (HttpClientErrorException e) {
            return ResponseEntity.badRequest().body(new DefaultResponse(order, e.getMessage()));
        }
        return restTemplate.postForEntity(ORDER_SERVICE_URL, order, DefaultResponse.class);
    }

    @GetMapping
    public ResponseEntity<List> getAllOrders() {
        return restTemplate.getForEntity(ORDER_SERVICE_URL, List.class);
    }

    @PostMapping("/order/findWorker/")
    public ResponseEntity<Integer> findWorker(@RequestParam String orderID) {
        String url = ORDER_SERVICE_URL + "/order/findWorker/?orderID=" + orderID;
        return restTemplate.postForEntity(url, null, Integer.class);
    }

    @PostMapping("/order/{orderId}/assignWorker/{workerId}")
    public ResponseEntity<AssignResponse> assignWorker(
            @PathVariable String orderId,
            @PathVariable String workerId) {
        String url = ORDER_SERVICE_URL + "/order/" + orderId + "/assignWorker/" + workerId;
        return restTemplate.postForEntity(url, null, AssignResponse.class);
    }
}