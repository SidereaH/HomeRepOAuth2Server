package homerep.homerepoauth.controllers.userservice;

import homerep.homerepoauth.config.HomeRepProperties;
import homerep.homerepoauth.models.userservice.Client;
import homerep.homerepoauth.models.userservice.dto.ClientResponse;
import homerep.homerepoauth.models.userservice.dto.GeoPair;
import homerep.homerepoauth.models.userservice.dto.GeoTimeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/users")
public class GatewayUsersController {

    private final RestTemplate restTemplate;


    private final String USER_SERVICE_URL;

    @Autowired
    public GatewayUsersController(RestTemplate restTemplate, HomeRepProperties props) {
        this.restTemplate = restTemplate;
        this.USER_SERVICE_URL = props.getUserservice() + "/clients";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody Client client) {
        try {
            HttpEntity<Client> request = new HttpEntity<>(client, createHeaders());
            ResponseEntity<Client> response = restTemplate.postForEntity(
                    USER_SERVICE_URL,
                    request,
                    Client.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllClients() {
        log.info(USER_SERVICE_URL);
        try {
            ResponseEntity<List<ClientResponse>> response = restTemplate.exchange(
                    USER_SERVICE_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ClientResponse>>() {}
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
    @GetMapping("/phone")
    public ResponseEntity<?> getClientByPhoneNumber(@RequestParam String phoneNumber) {
        try {
            ResponseEntity<ClientResponse> response = restTemplate.getForEntity(
                    USER_SERVICE_URL + "/phone?phoneNumber=" + phoneNumber,
                    ClientResponse.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) {
        try {
            ResponseEntity<ClientResponse> response = restTemplate.getForEntity(
                    USER_SERVICE_URL + "/" + id,
                    ClientResponse.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestBody Client client) {
        try {
            HttpEntity<Client> request = new HttpEntity<>(client, createHeaders());
            ResponseEntity<ClientResponse> response = restTemplate.exchange(
                    USER_SERVICE_URL + "/" + id,
                    HttpMethod.PUT,
                    request,
                    ClientResponse.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        try {
            restTemplate.delete(USER_SERVICE_URL + "/" + id);
            return ResponseEntity.noContent().build();
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @PostMapping("/{id}/location")
    public ResponseEntity<Void> updateClientLocation(
            @PathVariable Long id,
            @RequestParam double lat,
            @RequestParam double lng) {
        try {
            String url = String.format("%s/%d/location?lat=%f&lng=%f",
                    USER_SERVICE_URL, id, lat, lng);
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    url,
                    null,
                    Void.class
            );
            return ResponseEntity.status(response.getStatusCode()).build();
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<?> getClientLocation(@PathVariable Long id) {
        try {
            ResponseEntity<GeoPair> response = restTemplate.getForEntity(
                    USER_SERVICE_URL + "/" + id + "/location",
                    GeoPair.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/locations")
    public ResponseEntity<?> getLocationHistory(@RequestBody GeoTimeRequest timeRequest) {
        try {
            HttpEntity<GeoTimeRequest> request = new HttpEntity<>(timeRequest, createHeaders());
            ResponseEntity<GeoPair[]> response = restTemplate.exchange(
                    USER_SERVICE_URL + "/locations",
                    HttpMethod.GET,
                    request,
                    GeoPair[].class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/location/area")
    public ResponseEntity<?> getUsersInArea(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam int maxUsers) {
        try {
            String url = String.format("%s/location/area?lat=%f&lng=%f&maxUsers=%d",
                    USER_SERVICE_URL, lat, lng, maxUsers);
            ResponseEntity<Long[]> response = restTemplate.getForEntity(
                    url,
                    Long[].class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}