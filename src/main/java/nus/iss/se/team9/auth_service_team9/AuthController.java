package nus.iss.se.team9.auth_service_team9;

import jakarta.validation.Valid;
import nus.iss.se.team9.auth_service_team9.model.Member;
import nus.iss.se.team9.auth_service_team9.model.Status;
import nus.iss.se.team9.auth_service_team9.model.User;
import nus.iss.se.team9.auth_service_team9.service.UserService;
import nus.iss.se.team9.auth_service_team9.service.EmailService;
import nus.iss.se.team9.auth_service_team9.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private EmailService emailService;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${user.service.url}")
    private String userServiceUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        ResponseEntity<Map<String, Object>> response = userService.validateUser(requestBody);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            Boolean isValidLogin = (Boolean) responseBody.get("isValidLogin");
            String role = (String) responseBody.get("role");
            String status = (String) responseBody.get("status");

            if (isValidLogin != null && isValidLogin) {
                Integer userId = (Integer) responseBody.get("userId");
                String token = jwtService.generateJWT(username, userId, role);

                if (status != null && status.equals("deleted")) {
                    return ResponseEntity.status(403).body("Account has been deleted.");
                }

                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .body(role + " login successful, JWT token: " + token);
            } else {
                return ResponseEntity.status(401).body("Incorrect username or password.");
            }
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Failed to validate login.");
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
        // no email in request
        if (newMember.getEmail() == null || newMember.getEmail().isEmpty()) {
            String token = jwtService.generateJWT(newMember.getUsername(),newMember.getId(),"member");
            try {
                ResponseEntity<Map> userServiceResponse = userService.createMember(newMember,token);
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
        // email is exist in request
        String code = UserService.generateVerificationCode();
        try {
            ResponseEntity<String> emailResponse = emailService.sendVerifyCodeEmail(newMember,code);
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
        String token = jwtService.generateJWT(newMember.getUsername(), newMember.getId(), "member");
        String url = userServiceUrl + "/create";
        try {
            ResponseEntity<Map> response = userService.createMember(newMember,token);
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
