package za.co.pacifish.registration_service.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import za.co.pacifish.registration_service.dto.CreateCustomerRequest;
import za.co.pacifish.registration_service.dto.CustomerResponse;
import za.co.pacifish.registration_service.dto.FirebaseUserDetailsDto;
import za.co.pacifish.registration_service.dto.UpdateCustomerRequest;
import za.co.pacifish.registration_service.entity.Customer;
import za.co.pacifish.registration_service.repository.CustomerRepository;
import com.google.firebase.auth.UserRecord.UpdateRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FirebaseAuth firebaseAuth;

    public CustomerResponse createCustomer(CreateCustomerRequest request) throws FirebaseAuthException {
        var firebaseUserDetails = (FirebaseUserDetailsDto) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        String firebaseUid = firebaseUserDetails.firebaseUid();
        Optional<Customer> byFirebaseUid = customerRepository.findByFirebaseUid(firebaseUid);
        if (byFirebaseUid.isPresent()) {
            throw new IllegalStateException("Customer already exists.");
        }

        Customer customer = Customer.builder()
            .firebaseUid(firebaseUid)
            .email(firebaseUserDetails.email())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(request.phoneNumber())
            .build();

        log.info("Creating customer with firebase uid: {}", customer.getFirebaseUid());
        Customer save = customerRepository.save(customer);

        UpdateRequest updateRequest = new UpdateRequest(firebaseUid)
            .setDisplayName(request.firstName());
        firebaseAuth.updateUser(updateRequest);

        Map<String, Object> claims = new HashMap<>();
        claims.put("internal_user_id", save.getId().toString());
        FirebaseAuth.getInstance().setCustomUserClaims(firebaseUid, claims);

        return CustomerResponse.builder()
            .firebaseUid(save.getFirebaseUid())
            .email(save.getEmail())
            .firstName(save.getFirstName())
            .lastName(save.getLastName())
            .phoneNumber(save.getPhoneNumber())
            .build();
    }

    public Optional<CustomerResponse> getCustomerDetails() {
        var firebaseUserDetails = (FirebaseUserDetailsDto) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        log.info("Retrieving customer with firebase uid: {}", firebaseUserDetails.firebaseUid());
        return customerRepository.findByFirebaseUid(firebaseUserDetails.firebaseUid()).map(
            customer -> CustomerResponse.builder()
                .firebaseUid(customer.getFirebaseUid())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build()
        );
    }

    public CustomerResponse updateCustomer(UpdateCustomerRequest request) throws FirebaseAuthException {
        var firebaseUserDetails = (FirebaseUserDetailsDto) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getPrincipal();

        Customer customer = customerRepository.findByFirebaseUid(firebaseUserDetails.firebaseUid())
                .orElseThrow(() -> new IllegalStateException("Customer profile not found"));

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhoneNumber(request.phoneNumber());

        log.info("Updating customer with firebase uid: {}", customer.getFirebaseUid());
        Customer updated = customerRepository.save(customer);

        UpdateRequest updateRequest = new UpdateRequest(firebaseUserDetails.firebaseUid())
                .setDisplayName(request.firstName() + " " + request.lastName());
        firebaseAuth.updateUser(updateRequest);

        return CustomerResponse.builder()
                .firebaseUid(updated.getFirebaseUid())
                .email(updated.getEmail())
                .firstName(updated.getFirstName())
                .lastName(updated.getLastName())
                .phoneNumber(updated.getPhoneNumber())
                .build();
    }

    public boolean profileExists() {
        var firebaseUserDetails = (FirebaseUserDetailsDto) Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getPrincipal();

        return customerRepository.findByFirebaseUid(firebaseUserDetails.firebaseUid()).isPresent();
    }
}
