package homerep.homerepoauth.controllers;

import homerep.homerepoauth.models.Client;
import homerep.homerepoauth.models.GeoPair;
import homerep.homerepoauth.models.dto.GeoTimeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;


import java.util.List;

@RestController
@RequestMapping("/api/users")
public class GatewayUsersController {

    private final RestTemplate restTemplate;
    private static final String USER_SERVICE_URL = "http://localhost:8083/clients";

    @Autowired
    public GatewayUsersController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return restTemplate.postForEntity(USER_SERVICE_URL, client, Client.class);
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return restTemplate.exchange(
                USER_SERVICE_URL,
                HttpMethod.GET,
                null,
                new org.springframework.core.ParameterizedTypeReference<List<Client>>() {}
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return restTemplate.getForEntity(USER_SERVICE_URL + "/" + id, Client.class);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client client) {
        return restTemplate.exchange(
                USER_SERVICE_URL + "/" + id,
                HttpMethod.PUT,
                new HttpEntity<>(client),
                Client.class
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        restTemplate.delete(USER_SERVICE_URL + "/" + id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<Void> updateClientLocation(
            @PathVariable Long id,
            @RequestParam double lat,
            @RequestParam double lng) {
        return restTemplate.postForEntity(
                USER_SERVICE_URL + "/" + id + "/location?lat=" + lat + "&lng=" + lng,
                null,
                Void.class
        );
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<GeoPair> getClientLocation(@PathVariable Long id) {
        return restTemplate.getForEntity(
                USER_SERVICE_URL + "/" + id + "/location",
                GeoPair.class
        );
    }

    @GetMapping("/locations")
    public ResponseEntity<GeoPair[]> getLocationHistory(@RequestBody GeoTimeRequest timeRequest) {
        return restTemplate.postForEntity(
                USER_SERVICE_URL + "/locations",
                timeRequest,
                GeoPair[].class
        );
    }

    @GetMapping("/location/area")
    public ResponseEntity<Long[]> getUsersInArea(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int maxUsers) {
        return restTemplate.getForEntity(
                USER_SERVICE_URL + "/location/area?lat=" + lat + "&lng=" + lng + "&maxUsers=" + maxUsers,
                Long[].class
        );
    }
}