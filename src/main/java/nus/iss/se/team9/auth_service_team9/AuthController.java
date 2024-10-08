package nus.iss.se.team9.auth_service_team9;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.validation.Valid;
import nus.iss.se.team9.auth_service_team9.model.EmailDetails;
import nus.iss.se.team9.auth_service_team9.model.Member;
import nus.iss.se.team9.auth_service_team9.model.Status;
import nus.iss.se.team9.auth_service_team9.model.User;
import nus.iss.se.team9.auth_service_team9.service.AuthService;
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
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${email.service.url}")
    private String emailServiceUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        User user = authService.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String role = authService.checkIfAdmin(user) ? "admin" : "member";

            String token = Jwts.builder()
                    .setSubject(user.getUsername())
                    .claim("role", role)  // Adding role claim
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token valid for 10 hours
                    .signWith(SignatureAlgorithm.HS256, jwtSecret) // Use the same secret key as in filter
                    .compact();

            // Additional logic for member status
            if (role.equals("member") && authService.getMemberById(user.getId()).getMemberStatus() == Status.DELETED) {
                return ResponseEntity.status(403).body("Account has been deleted.");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(role + " login successful");
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

        // 检查输入数据是否有效
        if (bindingResult.hasErrors()) {
            response.put("message", "Invalid input data");
            response.put("errors", bindingResult.getFieldErrors());
            return ResponseEntity.badRequest().body(response);
        }

        newMember.setMemberStatus(Status.CREATED);

        // 处理没有email的用户（如允许这种情况）
        if (newMember.getEmail() == null || newMember.getEmail().isEmpty()) {
            authService.saveMember(newMember);

            // 生成JWT
            String token = Jwts.builder()
                    .setSubject(newMember.getUsername())
                    .claim("role", "member")  // 假设新用户的默认角色为 member
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token有效期10小时
                    .signWith(SignatureAlgorithm.HS256, jwtSecret)  // 使用密钥签名
                    .compact();

            // 返回JWT
            response.put("message", "Member registered successfully, please set your preferences.");
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)  // 将JWT添加到响应头部
                    .body(response);
        }

        // 处理有email的用户
        String code = AuthService.generateVerificationCode();
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setTo(newMember.getEmail());
        emailDetails.setSubject("Email Verify");
        emailDetails.setBody("Your Email Verification Code is :" + code);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<EmailDetails> request = new HttpEntity<>(emailDetails, headers);

        String url = emailServiceUrl;
        try {
            ResponseEntity<String> emailResponse = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (emailResponse.getStatusCode() == HttpStatus.OK) {
                // 邮件发送成功，生成JWT
//                authService.saveMember(newMember);
                String token = Jwts.builder()
                        .setSubject(newMember.getUsername())
                        .claim("role", "member")
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                        .signWith(SignatureAlgorithm.HS256, jwtSecret)
                        .compact();

                response.put("newMember", newMember);
                response.put("verifyCode", code);
                response.put("message", "Verification email sent. Please verify your email.");

                // 返回JWT
                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .body(response);
            } else {
                // 邮件发送失败
                response.put("message", "Failed to send verification email.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            // 捕获异常并返回错误信息
            response.put("message", "Error occurred while sending email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
