package homerep.homerepoauth.controllers.orderservice;


import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.PaymentType;
import homerep.homerepoauth.models.orderservice.dto.DefaultResponse;
import homerep.homerepoauth.services.orderservice.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class GatewayPaymentController {

    private final RestTemplate restTemplate;

    private  final String ORDER_SERVICE_URL;
    private final PaymentService paymentService;

    public GatewayPaymentController(RestTemplate restTemplate, HomeRepProperties props, PaymentService paymentService) {
        this.restTemplate = restTemplate;
        this.ORDER_SERVICE_URL = props.getOrderservice()+ "/payments";
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<List> getAllPayments() {
        return restTemplate.getForEntity(ORDER_SERVICE_URL, List.class);
    }

    @PostMapping
    public ResponseEntity<DefaultResponse> createPayment(@RequestBody PaymentType paymentType) {
        ResponseEntity<DefaultResponse> responseEntity;
        try{
            responseEntity = restTemplate.postForEntity(ORDER_SERVICE_URL, paymentType, DefaultResponse.class);
        }
        catch (HttpClientErrorException e){
            return  ResponseEntity.badRequest().body(new DefaultResponse("Error",e.getMessage()));
        }

        return responseEntity;
    }

    @PatchMapping("/activate")
    public ResponseEntity<DefaultResponse> activatePayment(@RequestParam String paymentName) {
        return ResponseEntity.ok(new DefaultResponse(paymentService.activatePayment(paymentName),"Payment has been activated."));
    }

    @PatchMapping("/deactivate")
    public ResponseEntity<DefaultResponse> deactivatePayment(@RequestParam String paymentName) {

        return ResponseEntity.ok(new DefaultResponse(paymentService.deactivatePayment(paymentName),"Payment has been deactivated."));
    }

    @DeleteMapping
    public ResponseEntity<DefaultResponse<PaymentType, String>> deletePayment(@RequestParam String paymentTypeName) {
        String url = ORDER_SERVICE_URL + "?paymentTypeName=" + paymentTypeName;
        restTemplate.delete(url);
        return ResponseEntity.noContent().build();
    }
}