package homerep.homerepoauth;

import homerep.homerepoauth.controllers.GatewayUsersController;
import homerep.homerepoauth.models.Client;
import homerep.homerepoauth.models.GeoPair;
import homerep.homerepoauth.models.dto.GeoTimeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GatewayUserControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GatewayUsersController gatewayUsersController;

    private static final String USER_SERVICE_URL = "http://user-service/clients";

    @Test
    void createClient_ShouldReturnCreatedClient() {
        Client client = new Client(1L, "John Doe");
        Client savedClient = new Client(1L, "John Doe");
        ResponseEntity<Client> response = new ResponseEntity<>(savedClient, HttpStatus.CREATED);

        when(restTemplate.postForEntity(eq(USER_SERVICE_URL), eq(client), eq(Client.class)))
                .thenReturn(response);

        ResponseEntity<Client> result = gatewayUsersController.createClient(client);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(savedClient, result.getBody());
        verify(restTemplate).postForEntity(USER_SERVICE_URL, client, Client.class);
    }

    @Test
    void getAllClients_ShouldReturnListOfClients() {
        List<Client> clients = List.of(
                new Client(1L, "John Doe"),
                new Client(2L, "Jane Smith")
        );
        ResponseEntity<List<Client>> response = new ResponseEntity<>(clients, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(USER_SERVICE_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(org.springframework.core.ParameterizedTypeReference.class)))
                .thenReturn(response);

        ResponseEntity<List<Client>> result = gatewayUsersController.getAllClients();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
        verify(restTemplate).exchange(
                eq(USER_SERVICE_URL),
                eq(HttpMethod.GET),
                isNull(),
                any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    void getClientById_ShouldReturnClient() {
        Long clientId = 1L;
        Client client = new Client(clientId, "John Doe");
        ResponseEntity<Client> response = new ResponseEntity<>(client, HttpStatus.OK);

        when(restTemplate.getForEntity(USER_SERVICE_URL + "/" + clientId, Client.class))
                .thenReturn(response);

        ResponseEntity<Client> result = gatewayUsersController.getClientById(clientId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(client, result.getBody());
        verify(restTemplate).getForEntity(USER_SERVICE_URL + "/" + clientId, Client.class);
    }

    @Test
    void updateClient_ShouldReturnUpdatedClient() {
        Long clientId = 1L;
        Client client = new Client(clientId, "Updated Name");
        ResponseEntity<Client> response = new ResponseEntity<>(client, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(USER_SERVICE_URL + "/" + clientId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Client.class)))
                .thenReturn(response);

        ResponseEntity<Client> result = gatewayUsersController.updateClient(clientId, client);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(client, result.getBody());
        verify(restTemplate).exchange(
                eq(USER_SERVICE_URL + "/" + clientId),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(Client.class));
    }

    @Test
    void deleteClient_ShouldReturnNoContent() {
        Long clientId = 1L;
        ResponseEntity<Void> result = gatewayUsersController.deleteClient(clientId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(restTemplate).delete(USER_SERVICE_URL + "/" + clientId);
    }

    @Test
    void updateClientLocation_ShouldReturnOk() {
        Long clientId = 1L;
        double lat = 55.7558;
        double lng = 37.6173;
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.OK);

        when(restTemplate.postForEntity(
                eq(USER_SERVICE_URL + "/" + clientId + "/location?lat=" + lat + "&lng=" + lng),
                isNull(),
                eq(Void.class)))
                .thenReturn(response);

        ResponseEntity<Void> result = gatewayUsersController.updateClientLocation(clientId, lat, lng);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(restTemplate).postForEntity(
                eq(USER_SERVICE_URL + "/" + clientId + "/location?lat=" + lat + "&lng=" + lng),
                isNull(),
                eq(Void.class));
    }

    @Test
    void getClientLocation_ShouldReturnGeoPair() {
        Long clientId = 1L;
        GeoPair geoPair = new GeoPair(55.7558, 37.6173);
        ResponseEntity<GeoPair> response = new ResponseEntity<>(geoPair, HttpStatus.OK);

        when(restTemplate.getForEntity(
                USER_SERVICE_URL + "/" + clientId + "/location",
                GeoPair.class))
                .thenReturn(response);

        ResponseEntity<GeoPair> result = gatewayUsersController.getClientLocation(clientId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(geoPair, result.getBody());
        verify(restTemplate).getForEntity(
                USER_SERVICE_URL + "/" + clientId + "/location",
                GeoPair.class);
    }


    @Test
    void getUsersInArea_ShouldReturnUserIds() {
        double lat = 55.7558;
        double lng = 37.6173;
        int maxUsers = 10;
        Long[] userIds = {1L, 2L, 3L};
        ResponseEntity<Long[]> response = new ResponseEntity<>(userIds, HttpStatus.OK);

        when(restTemplate.getForEntity(
                eq(USER_SERVICE_URL + "/location/area?lat=" + lat + "&lng=" + lng + "&maxUsers=" + maxUsers),
                eq(Long[].class)))
                .thenReturn(response);

        ResponseEntity<Long[]> result = gatewayUsersController.getUsersInArea(lat, lng, maxUsers);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertArrayEquals(userIds, result.getBody());
        verify(restTemplate).getForEntity(
                eq(USER_SERVICE_URL + "/location/area?lat=" + lat + "&lng=" + lng + "&maxUsers=" + maxUsers),
                eq(Long[].class));
    }
}