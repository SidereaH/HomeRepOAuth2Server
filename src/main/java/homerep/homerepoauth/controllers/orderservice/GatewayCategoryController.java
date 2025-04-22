package homerep.homerepoauth.controllers.orderservice;

import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.Category;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class GatewayCategoryController {

    private final RestTemplate restTemplate;
    private final String ORDER_SERVICE_URL;

    public GatewayCategoryController(RestTemplate restTemplate, HomeRepProperties props) {
        this.restTemplate = restTemplate;
        this.ORDER_SERVICE_URL = props.getOrderservice() + "/categories";
    }

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        ParameterizedTypeReference<List<Category>> responseType =
                new ParameterizedTypeReference<List<Category>>() {};

        try {
            ResponseEntity<List<Category>> response = restTemplate.exchange(
                    ORDER_SERVICE_URL,
                    HttpMethod.GET,
                    null,
                    responseType
            );

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Category> request = new HttpEntity<>(category, headers);

        try {
            ResponseEntity<Category> response = restTemplate.exchange(
                    ORDER_SERVICE_URL,
                    HttpMethod.POST,
                    request,
                    Category.class
            );

            return ResponseEntity
                    .status(response.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity
                    .badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(category);
        }
    }
}