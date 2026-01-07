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
import za.co.pacifish.registration_service.entity.Customer;
import za.co.pacifish.registration_service.repository.CustomerRepository;
import com.google.firebase.auth.UserRecord.UpdateRequest;

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
        Optional<Customer> byFirebaseUid = customerRepository.findByFirebaseUid(firebaseUserDetails.firebaseUid());
        if (byFirebaseUid.isPresent()) {
            throw new IllegalStateException("Customer already exists.");
        }

        Customer customer = Customer.builder()
            .firebaseUid(firebaseUserDetails.firebaseUid())
            .email(firebaseUserDetails.email())
            .firstName(request.firstName())
            .lastName(request.lastName())
            .phoneNumber(request.phoneNumber())
            .build();

        log.info("Creating customer with firebase uid: {}", customer.getFirebaseUid());
        Customer save = customerRepository.save(customer);

        UpdateRequest updateRequest = new UpdateRequest(firebaseUserDetails.firebaseUid())
            .setDisplayName(request.firstName());
        firebaseAuth.updateUser(updateRequest);

        return CustomerResponse.builder()
            .firebaseUid(save.getFirebaseUid())
            .email(save.getEmail())
            .firstName(save.getFirstName())
            .lastName(save.getLastName())
            .phoneNumber(save.getPhoneNumber())
            .build();
    }

    public Optional<CustomerResponse> getCustomerDetails(String firebaseUid) {
        log.info("Retrieving customer with firebase uid: {}", firebaseUid);
        return customerRepository.findByFirebaseUid(firebaseUid).map(
            customer -> CustomerResponse.builder()
                .firebaseUid(customer.getFirebaseUid())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .phoneNumber(customer.getPhoneNumber())
                .build()
        );
    }
}
