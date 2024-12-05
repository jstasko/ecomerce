package sk.stasko.ecomerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record SignupRequest(
        @NotBlank
        @Email
        @Size(max = 50)
        String email,

        @NotBlank
        @Size(max = 120)
        String password,

        @NotBlank
        @Size(max = 20)
        String username,

        Set<String> roles
) {
}
