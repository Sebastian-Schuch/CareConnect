package at.ac.tuwien.sepr.groupphase.backend.endpoint.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangePasswordDto(
    @NotNull(message = "Email must not be null")
    @Email
    String email,

    String oldPassword,
    
    @NotNull(message = "Password must not be null")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword
) {
}
