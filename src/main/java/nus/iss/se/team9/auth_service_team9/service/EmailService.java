package nus.iss.se.team9.auth_service_team9.service;

import nus.iss.se.team9.auth_service_team9.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {
    @Value("${email.service.url}")
    private String emailServiceUrl;

    private final RestTemplate restTemplate;
    @Autowired
    public EmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> sendVerifyCodeEmail(String email, String code){
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setTo(email);
        emailDetails.setSubject("Email Verify");
        emailDetails.setBody("Your Email Verification Code is :" + code);
        String url = emailServiceUrl + "/sendEmailOTP";
        return restTemplate.postForEntity(url, emailDetails, String.class);
    }
}
