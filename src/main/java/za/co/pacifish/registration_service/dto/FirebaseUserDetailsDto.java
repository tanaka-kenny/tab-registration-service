package za.co.pacifish.registration_service.dto;

public record FirebaseUserDetailsDto(
    String email,
    String firebaseUid
) {
}