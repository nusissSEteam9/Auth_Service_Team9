package nus.iss.se.team9.auth_service_team9;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import nus.iss.se.team9.auth_service_team9.model.EmailDetails;
import nus.iss.se.team9.auth_service_team9.model.Member;
import nus.iss.se.team9.auth_service_team9.model.Status;
import nus.iss.se.team9.auth_service_team9.model.User;
import nus.iss.se.team9.auth_service_team9.service.AuthService;
import nus.iss.se.team9.auth_service_team9.service.EmailService;
import nus.iss.se.team9.auth_service_team9.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${email.service.url}")
    private String emailServiceUrl;
    @Value("${user.service.url}")
    private String userServiceUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        User user = authService.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String role = authService.checkIfAdmin(user) ? "admin" : "member";
            String token = jwtService.generateJWT(username,role);
            if (role.equals("member") && authService.getMemberById(user.getId()).getMemberStatus() == Status.DELETED) {
                return ResponseEntity.status(403).body("Account has been deleted.");
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(role + " login successful, JWT token :" + token);
        } else {
            return ResponseEntity.status(401).body("Incorrect username or password.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful.");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerMember(@Valid @RequestBody Member newMember,
                                                              BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("message", "Invalid input data");
            response.put("errors", bindingResult.getFieldErrors());
            return ResponseEntity.badRequest().body(response);
        }

        newMember.setMemberStatus(Status.CREATED);
        if (newMember.getEmail() == null || newMember.getEmail().isEmpty()) {
            String token = jwtService.generateJWT(newMember.getUsername(),"member");
            String url = userServiceUrl + "/create";
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + token);
                HttpEntity<Member> request = new HttpEntity<>(newMember, headers);
                ResponseEntity<Map> userServiceResponse = restTemplate.postForEntity(url, request, Map.class);
                if (userServiceResponse.getStatusCode() != HttpStatus.OK) {
                    throw new RuntimeException("Failed to create user: " + userServiceResponse.getStatusCode());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while creating user: " + e.getMessage());
            }
            response.put("message", "Member registered successfully, please set your preferences.");
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(response);
        }

        String code = AuthService.generateVerificationCode();
        EmailDetails emailDetails = emailService.generateVerifyCodeEmailObj(newMember,code);
        String url = emailServiceUrl + "/sendEmailOTP";
        try {
            ResponseEntity<String> emailResponse = restTemplate.postForEntity(url, emailDetails, String.class);
            if (emailResponse.getStatusCode() == HttpStatus.OK) {
                response.put("newMember", newMember);
                response.put("verifyCode", code);
                response.put("message", "Verification email sent. Please verify your email.");
                return ResponseEntity.ok().body(response);
            } else {
                response.put("message", "Failed to send verification email.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("message", "Error occurred while sending email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/verifyCode/success")
    public ResponseEntity<?> verifyEmailDone(@RequestBody Member newMember) {
        String token = jwtService.generateJWT(newMember.getUsername(),"member");
        String url = userServiceUrl + "/create";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<Member> request = new HttpEntity<>(newMember, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(token);
            } else {
                throw new RuntimeException("Failed to create user: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while creating user: " + e.getMessage());
        }
    }

}
