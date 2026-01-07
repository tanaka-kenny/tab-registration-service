package za.co.pacifish.registration_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.pacifish.registration_service.dto.CustomerResponse;
import za.co.pacifish.registration_service.service.CustomerService;

@RestController
@RequestMapping("/api/web/customers")
@RequiredArgsConstructor
public class CustomerWebController {
    private final CustomerService customerService;

    @GetMapping("/{customerFirebaseUid}")
    public ResponseEntity<CustomerResponse> getCustomerByFirebaseUid(
        @PathVariable String customerFirebaseUid) {
        return customerService.getCustomerDetails(customerFirebaseUid)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
