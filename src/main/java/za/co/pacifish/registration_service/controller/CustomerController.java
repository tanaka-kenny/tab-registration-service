package za.co.pacifish.registration_service.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import za.co.pacifish.registration_service.dto.CheckProfileExistsResponse;
import za.co.pacifish.registration_service.dto.CreateCustomerRequest;
import za.co.pacifish.registration_service.dto.CustomerResponse;
import za.co.pacifish.registration_service.dto.UpdateCustomerRequest;
import za.co.pacifish.registration_service.service.CustomerService;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomerProfile(
            @RequestBody CreateCustomerRequest request,
            Authentication authentication
    ) throws FirebaseAuthException {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request));
    }

    @GetMapping
    public ResponseEntity<CustomerResponse> getCustomerProfile(
            Authentication authentication
    ) {
        return customerService.getCustomerDetails()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<CustomerResponse> updateCustomerProfile(
            @RequestBody UpdateCustomerRequest request,
            Authentication authentication
    ) throws FirebaseAuthException {
        return ResponseEntity.ok(
                customerService.updateCustomer(request)
        );
    }

    @GetMapping("/exists")
    public ResponseEntity<CheckProfileExistsResponse> checkProfileExists(
            Authentication authentication
    ) {
        boolean exists = customerService.profileExists();
        return ResponseEntity.ok(new CheckProfileExistsResponse(exists));
    }
}