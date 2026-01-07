package za.co.pacifish.registration_service.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.co.pacifish.registration_service.dto.CreateCustomerRequest;
import za.co.pacifish.registration_service.dto.CustomerResponse;
import za.co.pacifish.registration_service.dto.FirebaseUserDetailsDto;
import za.co.pacifish.registration_service.service.CustomerService;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PutMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request)
        throws FirebaseAuthException {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomer(
        Authentication authentication
    ) {
        FirebaseUserDetailsDto firebaseUser = (FirebaseUserDetailsDto) authentication.getPrincipal();
        return customerService.getCustomerDetails(firebaseUser.firebaseUid())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{firebaseUid}")
    public ResponseEntity<CustomerResponse> getCustomerByFirebaseUid(
        @PathVariable String firebaseUid) {
        return customerService.getCustomerDetails(firebaseUid)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
