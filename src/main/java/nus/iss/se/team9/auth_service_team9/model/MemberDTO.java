package nus.iss.se.team9.auth_service_team9.model;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class MemberDTO {

    @Size(min = 3, message = "Username must be at least 3 characters")
    private String username;

    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*\\p{Punct}).{8,}$", message = "Password must be at least 8 characters long, "
            + "contain a number, and have at least one punctuation.")
    private String password;

    @Email(message = "Invalid email format")
    @Pattern(regexp = "|^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "Invalid email format")
    private String email;

    // Getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
