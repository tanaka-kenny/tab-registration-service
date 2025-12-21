package za.co.pacifish.registration_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank String phoneNumber
) {
}
