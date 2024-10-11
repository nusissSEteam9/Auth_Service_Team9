package nus.iss.se.team9.auth_service_team9.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    @Autowired
    public UserService(RestTemplate restTemplate, @Value("${user.service.url}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public ResponseEntity<Integer> createMember(Map<String, String> memberData) {
        String url = userServiceUrl + "/create";
        try {
            String username = memberData.get("username");
            String password = memberData.get("password");
            String email = memberData.get("email");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Integer> response = restTemplate.postForEntity(url, request, Integer.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                System.out.println("Unexpected response status: " + response.getStatusCode());
                return ResponseEntity.status(response.getStatusCode()).body(null);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.out.println("Error response from server: " + e.getStatusCode());
            System.out.println("Error body: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public ResponseEntity<Map<String, Object>> validateUser(Map<String, String> credentials) {
        String url = userServiceUrl + "/validate-login";
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, credentials, Map.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public boolean validateUsername(String username) {
        System.out.println("Validate Username");
        String url = userServiceUrl + "/validate-username/" + username;
        try {
            ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                System.out.println(response.getBody());
                return Boolean.TRUE.equals(response.getBody());
            } else {
                throw new RuntimeException("Failed to check username, status code: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error occurred while checking username: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }

    public static String generateVerificationCode() {
        int codeLength = 4;
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            int digit = random.nextInt(10);
            codeBuilder.append(digit);
        }
        return codeBuilder.toString();
    }
}