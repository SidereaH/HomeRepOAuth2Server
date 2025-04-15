package homerep.homerepoauth.controllers.orderservice;


import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.orderservice.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class GatewayCategoryController {

    private final RestTemplate restTemplate;

    private final String ORDER_SERVICE_URL ;

    public GatewayCategoryController(RestTemplate restTemplate, HomeRepProperties props) {
        this.restTemplate = restTemplate;
        this.ORDER_SERVICE_URL = props.getOrderservice() +"/categories";
    }

    @GetMapping
    public ResponseEntity<List> getAllCategories() {
        return restTemplate.getForEntity(ORDER_SERVICE_URL, List.class);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        ResponseEntity<Category> resp;
        try {
            resp =  restTemplate.postForEntity(ORDER_SERVICE_URL, category, Category.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(category);
        }
        resp =restTemplate.postForEntity(ORDER_SERVICE_URL, category, Category.class);
        return resp;
    }
}