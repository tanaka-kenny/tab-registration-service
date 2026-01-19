package za.co.pacifish.registration_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.pacifish.registration_service.entity.Company;
import za.co.pacifish.registration_service.entity.Customer;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUuid(String firebaseUid);
}
