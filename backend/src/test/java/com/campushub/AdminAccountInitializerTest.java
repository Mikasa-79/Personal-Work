package com.campushub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminAccountInitializerTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void seededAdminCanAccessAdminEndpoints() {
        ResponseEntity<Map> loginResponse = rest.postForEntity(
                "/api/auth/login",
                Map.of("studentNo", "admin", "password", "123456"),
                Map.class
        );

        assertEquals(200, loginResponse.getStatusCodeValue());
        String token = (String) loginResponse.getBody().get("token");
        assertNotNull(token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        ResponseEntity<List> usersResponse = rest.exchange(
                "/api/admin/users",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        assertEquals(200, usersResponse.getStatusCodeValue());
    }
}
