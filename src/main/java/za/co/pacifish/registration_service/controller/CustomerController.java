package za.co.pacifish.registration_service.controller;

import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.pacifish.registration_service.dto.CreateCustomerRequest;
import za.co.pacifish.registration_service.dto.CustomerResponse;
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
    public ResponseEntity<CustomerResponse> getCustomer() {
        return customerService.getCustomerDetails()
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
