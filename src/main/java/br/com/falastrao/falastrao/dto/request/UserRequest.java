package br.com.falastrao.falastrao.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserRequest (
        @NotEmpty(message = "Username is required.")
        @Size(min = 1, max = 50)
        String username,

        @NotEmpty(message = "Email is required.")
        @Email(message = "Email should be valid.")
        String email,

        @NotEmpty(message = "Password is required.")
        @Size(min = 6, message = "Password must be at least 6 characters long.")
        String password
){}
