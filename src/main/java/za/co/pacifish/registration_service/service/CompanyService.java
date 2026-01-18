package za.co.pacifish.registration_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.pacifish.registration_service.dto.CompanyResponse;
import za.co.pacifish.registration_service.dto.CreateCompanyRequest;
import za.co.pacifish.registration_service.entity.Company;
import za.co.pacifish.registration_service.repository.CompanyRepository;
import za.co.pacifish.registration_service.repository.CustomerRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;

    public Optional<CompanyResponse> addCompany(CreateCompanyRequest request, String customerUuid) {
        return customerRepository.findByFirebaseUid(customerUuid)
            .map(customer -> {
                Company company = Company.builder()
                    .uuid(UUID.randomUUID().toString())
                    .name(request.name())
                    .street1(request.street1())
                    .street2(request.street2())
                    .suburb(request.suburb())
                    .postalCode(request.postalCode())
                    .city(request.city())
                    .province(request.province())
                    .customer(customer)
                    .build();

                companyRepository.save(company);
                log.info("Added company: {} for customer: {}", company.getUuid(), customerUuid);

                return CompanyResponse.builder()
                    .name(company.getUuid())
                    .build();
            });


    }
}
