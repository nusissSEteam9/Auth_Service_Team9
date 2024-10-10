package nus.iss.se.team9.auth_service_team9.service;

import nus.iss.se.team9.auth_service_team9.model.Member;
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

    public ResponseEntity<Integer> createMember(Map<String, String> memberData) {
        String url = userServiceUrl + "/create";
        try {
            String username = memberData.get("username");
            String password = memberData.get("password");
            String email = memberData.get("email");

            // 构建请求的 JSON 数据
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("username", username);
            requestBody.put("password", password);
            requestBody.put("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 构建请求实体，包含 JSON 数据和 headers
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // 发送 POST 请求并期待返回的 Member 对象
            ResponseEntity<Integer> response = restTemplate.postForEntity(url, request, Integer.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                // 返回创建的 Member 对象
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