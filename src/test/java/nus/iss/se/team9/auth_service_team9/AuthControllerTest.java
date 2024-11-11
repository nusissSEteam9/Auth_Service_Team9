package nus.iss.se.team9.auth_service_team9;

import nus.iss.se.team9.auth_service_team9.model.MemberDTO;
import nus.iss.se.team9.auth_service_team9.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JWTService jwtService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLogin_Success() {
        Map<String, String> credentials = Map.of("username", "testUser", "password", "testPassword");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isValidLogin", true);
        responseBody.put("role", "user");
        responseBody.put("userId", 1);

        ResponseEntity<Map<String, Object>> userServiceResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(userService.validateUser(any())).thenReturn(userServiceResponse);
        when(jwtService.generateJWT("testUser", 1, "user")).thenReturn("testToken");

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService, times(1)).validateUser(any());
    }

    @Test
    void testLogin_InvalidCredentials() {
        Map<String, String> credentials = Map.of("username", "testUser", "password", "wrongPassword");
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isValidLogin", false);

        ResponseEntity<Map<String, Object>> userServiceResponse = new ResponseEntity<>(responseBody, HttpStatus.OK);
        when(userService.validateUser(any())).thenReturn(userServiceResponse);

        ResponseEntity<?> response = authController.login(credentials);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Incorrect username or password.", response.getBody());
    }

    @Test
    void testLogout() {
        ResponseEntity<String> response = authController.logout();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Logout successful.", response.getBody());
    }


    @Test
    void testRegisterMember_Fail_UsernameTaken() {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername("existingUser");
        memberDTO.setPassword("password123");

        when(userService.validateUsername("existingUser")).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = authController.registerMember(memberDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username is already taken.", response.getBody().get("message"));
    }

    @Test
    void testVerifyEmailDone_Success() {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername("verifiedUser");
        memberDTO.setPassword("password123");
        memberDTO.setEmail("user@example.com");

        Map<String, String> memberData = new HashMap<>();
        memberData.put("username", "verifiedUser");
        memberData.put("password", "password123");
        memberData.put("email", "user@example.com");

        ResponseEntity<Integer> userServiceResponse = new ResponseEntity<>(1, HttpStatus.OK);
        when(userService.createMember(memberData)).thenReturn(userServiceResponse);
        when(jwtService.generateJWT("verifiedUser", 1, "member")).thenReturn("token");

        ResponseEntity<?> response = authController.verifyEmailDone(memberDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((Map<?, ?>) response.getBody()).get("memberId"));
    }

    @Test
    void testVerifyEmailDone_MissingEmail() {
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername("verifiedUser");
        memberDTO.setPassword("password123");

        ResponseEntity<?> response = authController.verifyEmailDone(memberDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required.", ((Map<?, ?>) response.getBody()).get("message"));
    }
}