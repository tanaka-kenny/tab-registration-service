package za.co.pacifish.registration_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.pacifish.registration_service.entity.Customer;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByFirebaseUid(String firebaseUid);
}
