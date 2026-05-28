package com.campushub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderFlowIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void requesterAndProviderCanCompleteReviewedOrderFlow() {
        AuthSession requester = register("req");
        AuthSession provider = register("pro");
        AuthSession admin = login("admin", "123456");

        Map<String, Object> createRequest = Map.of(
                "title", "Pick up express package",
                "description", "Please help pick up a package from the campus station.",
                "location", "Campus express station",
                "expectedTime", LocalDateTime.now().plusDays(1).withNano(0).toString(),
                "category", "EXPRESS_PICKUP",
                "reward", 5
        );
        ResponseEntity<Map> createdRequest = post("/api/requests", createRequest, requester.token(), Map.class);
        assertEquals(201, createdRequest.getStatusCodeValue());
        Long requestId = numberValue(createdRequest.getBody(), "id");
        assertNotNull(requestId);
        assertEquals("OPEN", createdRequest.getBody().get("status"));

        ResponseEntity<Map> approvedRequest = post("/api/admin/requests/" + requestId + "/approve", null, admin.token(), Map.class);
        assertEquals(200, approvedRequest.getStatusCodeValue());
        assertEquals("OPEN", approvedRequest.getBody().get("status"));

        ResponseEntity<Map> acceptedOrder = post("/api/orders/" + requestId + "/accept", null, provider.token(), Map.class);
        assertEquals(200, acceptedOrder.getStatusCodeValue());
        Long orderId = numberValue(acceptedOrder.getBody(), "id");
        assertNotNull(orderId);
        assertEquals("ACCEPTED", acceptedOrder.getBody().get("status"));
        assertEquals(requestId, numberValue(acceptedOrder.getBody(), "requestId"));
        assertEquals(requester.userId(), numberValue(acceptedOrder.getBody(), "requesterId"));
        assertEquals(provider.userId(), numberValue(acceptedOrder.getBody(), "providerId"));

        ResponseEntity<Map> confirmedOrder = post("/api/orders/" + orderId + "/confirm", null, requester.token(), Map.class);
        assertEquals(200, confirmedOrder.getStatusCodeValue());
        assertEquals("CONFIRMED", confirmedOrder.getBody().get("status"));

        ResponseEntity<Map> startedOrder = post("/api/orders/" + orderId + "/start", null, provider.token(), Map.class);
        assertEquals(200, startedOrder.getStatusCodeValue());
        assertEquals("IN_PROGRESS", startedOrder.getBody().get("status"));

        ResponseEntity<Map> completedOrder = post("/api/orders/" + orderId + "/complete", null, requester.token(), Map.class);
        assertEquals(200, completedOrder.getStatusCodeValue());
        assertEquals("COMPLETED", completedOrder.getBody().get("status"));

        Map<String, Object> review = Map.of(
                "rating", 5,
                "comment", "Service was fast and reliable."
        );
        ResponseEntity<Map> reviewedOrder = post("/api/orders/" + orderId + "/reviews", review, requester.token(), Map.class);
        assertEquals(200, reviewedOrder.getStatusCodeValue());
        assertEquals("REVIEWED", reviewedOrder.getBody().get("status"));

        ResponseEntity<Map> fetchedOrder = get("/api/orders/" + orderId, provider.token(), Map.class);
        assertEquals(200, fetchedOrder.getStatusCodeValue());
        assertEquals("REVIEWED", fetchedOrder.getBody().get("status"));

        ResponseEntity<List> providerNotifications = get("/api/notifications", provider.token(), List.class);
        assertEquals(200, providerNotifications.getStatusCodeValue());
        assertFalse(providerNotifications.getBody().isEmpty());
    }

    private AuthSession register(String prefix) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        Map<String, Object> payload = Map.of(
                "studentNo", prefix + suffix,
                "password", "password123",
                "nickname", prefix + "-user"
        );
        ResponseEntity<Map> response = rest.postForEntity("/api/auth/register", payload, Map.class);
        assertEquals(201, response.getStatusCodeValue());
        return authSession(response.getBody());
    }

    private AuthSession login(String studentNo, String password) {
        ResponseEntity<Map> response = rest.postForEntity(
                "/api/auth/login",
                Map.of("studentNo", studentNo, "password", password),
                Map.class
        );
        assertEquals(200, response.getStatusCodeValue());
        return authSession(response.getBody());
    }

    private AuthSession authSession(Map body) {
        assertNotNull(body);
        return new AuthSession(
                (String) body.get("token"),
                numberValue(body, "userId")
        );
    }

    private <T> ResponseEntity<T> post(String url, Object body, String token, Class<T> responseType) {
        return rest.exchange(url, HttpMethod.POST, entity(body, token), responseType);
    }

    private <T> ResponseEntity<T> get(String url, String token, Class<T> responseType) {
        return rest.exchange(url, HttpMethod.GET, entity(null, token), responseType);
    }

    private HttpEntity<Object> entity(Object body, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(body, headers);
    }

    private Long numberValue(Map body, String key) {
        assertNotNull(body);
        Object value = body.get(key);
        assertNotNull(value);
        return ((Number) value).longValue();
    }

    private record AuthSession(String token, Long userId) {
    }
}
