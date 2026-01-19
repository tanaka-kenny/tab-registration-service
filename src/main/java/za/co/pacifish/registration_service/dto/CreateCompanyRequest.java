package za.co.pacifish.registration_service.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCompanyRequest(
    @NotBlank String name,
    @NotBlank String street1,
    @NotBlank String street2,
    @NotBlank String suburb,
    @NotBlank String city,
    @NotBlank String postalCode,
    @NotBlank String province,
    @NotBlank String customerUuid
) {
}
