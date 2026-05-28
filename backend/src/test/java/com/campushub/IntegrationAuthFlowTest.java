package com.campushub;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationAuthFlowTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    public void authRefreshLogoutFlow() {
        // register
        Map<String, Object> reg = Map.of(
                "studentNo", "s100000",
                "password", "password123",
                "nickname", "tester"
        );
        ResponseEntity<String> regRespStr = rest.postForEntity("/api/auth/register", reg, String.class);
        assertEquals(201, regRespStr.getStatusCodeValue());
        // print raw response to help diagnose encoding/content issues when tests fail
        System.out.println("REGISTER RAW: " + regRespStr.getBody());
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        Map<String, Object> regBody;
        try {
            regBody = mapper.readValue(regRespStr.getBody(), Map.class);
        } catch (Exception ex) {
            throw new RuntimeException("无法解析注册响应为 JSON: " + ex.getMessage(), ex);
        }
        String token = (String) regBody.get("token");
        String refresh = (String) regBody.get("refreshToken");

        // access protected endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        ResponseEntity<List> ordersResp = rest.exchange("/api/orders", HttpMethod.GET, new HttpEntity<>(headers), List.class);
        assertEquals(200, ordersResp.getStatusCodeValue());

        // refresh
        Map<String, String> refreshReq = Map.of("refreshToken", refresh);
        ResponseEntity<Map> refreshResp = rest.postForEntity("/api/auth/refresh", refreshReq, Map.class);
        assertEquals(200, refreshResp.getStatusCodeValue());
        String newToken = (String) refreshResp.getBody().get("token");
        String newRefresh = (String) refreshResp.getBody().get("refreshToken");

        // using old refresh should fail (rotated)
        ResponseEntity<String> oldRefreshResp = rest.postForEntity("/api/auth/refresh", refreshReq, String.class);
        assertEquals(400, oldRefreshResp.getStatusCodeValue());

        // logout with new token
        HttpHeaders headers2 = new HttpHeaders();
        headers2.set("Authorization", "Bearer " + newToken);
        ResponseEntity<Void> logoutResp = rest.exchange("/api/auth/logout", HttpMethod.POST, new HttpEntity<>(headers2), Void.class);
        assertEquals(204, logoutResp.getStatusCodeValue());

        // refresh with new refresh token after logout should fail
        Map<String, String> newRefreshReq = Map.of("refreshToken", newRefresh);
        ResponseEntity<String> postLogoutRefresh = rest.postForEntity("/api/auth/refresh", newRefreshReq, String.class);
        assertEquals(400, postLogoutRefresh.getStatusCodeValue());
    }
}
