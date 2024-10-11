package nus.iss.se.team9.auth_service_team9;

import jakarta.validation.Valid;
import nus.iss.se.team9.auth_service_team9.model.*;
import nus.iss.se.team9.auth_service_team9.service.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JWTService jwtService;
    private final EmailService emailService;

    @Autowired
    public AuthController(UserService userService, JWTService jwtService, EmailService emailService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

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
            assert responseBody != null;
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

    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful.");
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerMember(@Valid @RequestBody MemberDTO memberDTO) {
        Map<String, Object> response = new HashMap<>();

        if (memberDTO.getUsername() == null || memberDTO.getUsername().isEmpty()) {
            response.put("message", "Username is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (memberDTO.getPassword() == null || memberDTO.getPassword().isEmpty()) {
            response.put("message", "Password is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (userService.validateUsername(memberDTO.getUsername())) {
            response.put("message", "Username is already taken.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        // No email in request
        if (memberDTO.getEmail() == null || memberDTO.getEmail().isEmpty()) {
            System.out.println("No email");
            Map<String, String> memberData = new HashMap<>();
            memberData.put("username", memberDTO.getUsername());
            memberData.put("password", memberDTO.getPassword());
            memberData.put("email", memberDTO.getEmail());
            try {
                ResponseEntity<Integer> userServiceResponse = userService.createMember(memberData);
                Integer createdMemberId = userServiceResponse.getBody();
                String token = jwtService.generateJWT(memberDTO.getUsername(), createdMemberId, "member");
                response.put("message", "Member registered successfully, please set your preferences.");
                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .body(response);
            } catch (Exception e) {
                throw new RuntimeException("Error occurred while creating user: " + e.getMessage());
            }
        }

        // Email exists, validate email
        System.out.println("email need to be validated");
        String code = UserService.generateVerificationCode();
        try {
            ResponseEntity<String> emailResponse = emailService.sendVerifyCodeEmail(memberDTO.getEmail(), code);
            if (emailResponse.getStatusCode() == HttpStatus.OK) {
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
    public ResponseEntity<?> verifyEmailDone(@RequestBody MemberDTO memberDTO) {
        Map<String, Object> response = new HashMap<>();

        if (memberDTO.getUsername() == null || memberDTO.getUsername().isEmpty()) {
            response.put("message", "Username is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (memberDTO.getPassword() == null || memberDTO.getPassword().isEmpty()) {
            response.put("message", "Password is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (memberDTO.getEmail() == null || memberDTO.getEmail().isEmpty()) {
            response.put("message", "Email is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (userService.validateUsername(memberDTO.getUsername())) {
            response.put("message", "Username is already taken.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, String> memberData = new HashMap<>();
        memberData.put("username", memberDTO.getUsername());
        memberData.put("password", memberDTO.getPassword());
        memberData.put("email", memberDTO.getEmail());

        try {
            ResponseEntity<Integer> responseEntity = userService.createMember(memberData);
            String token = jwtService.generateJWT(memberDTO.getUsername(), responseEntity.getBody(), "member");
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("JWT token :" + token + "\n new memberId : " + responseEntity.getBody());
            } else {
                throw new RuntimeException("Failed to create user: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            response.put("message", "Error occurred while creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
