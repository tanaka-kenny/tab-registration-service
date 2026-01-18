package za.co.pacifish.registration_service.dto;

import lombok.Builder;

@Builder
public record CompanyResponse(
    String uuid,
    String name,
    String street1,
    String street2,
    String suburb,
    String city,
    String postalCode,
    String province
) {
}
