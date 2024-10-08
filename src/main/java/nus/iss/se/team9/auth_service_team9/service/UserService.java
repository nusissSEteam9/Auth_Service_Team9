package nus.iss.se.team9.auth_service_team9.service;

import nus.iss.se.team9.auth_service_team9.model.Admin;
import nus.iss.se.team9.auth_service_team9.model.Member;
import nus.iss.se.team9.auth_service_team9.model.User;

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

    @Autowired
    private RestTemplate restTemplate;
    @Value("${user.service.url}")
    private String userServiceUrl;

    public User searchUserByUsername(String username) {
        String url = userServiceUrl + "/search/user";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        ResponseEntity<User> response = restTemplate.postForEntity(url, requestBody, User.class);
        return response.getBody();
    }

    public Member searchMemberById(Integer id) {
        String url = userServiceUrl + "/search/member";
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("id", id);
        ResponseEntity<Member> response = restTemplate.postForEntity(url, requestBody, Member.class);
        return response.getBody();
    }

    public ResponseEntity<Map> createMember(Member newMember, String token) {
        String url = userServiceUrl + "/create";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Member> request = new HttpEntity<>(newMember, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    public boolean checkIfAdmin(User user) {
        if (user instanceof Admin) {
            return true;
        } else if (user instanceof Member) {
            return false;
        }
        return false;
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