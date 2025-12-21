package za.co.pacifish.registration_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {
    private String firebaseUid;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
