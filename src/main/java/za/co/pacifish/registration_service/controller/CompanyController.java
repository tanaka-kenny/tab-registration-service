package za.co.pacifish.registration_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.co.pacifish.registration_service.dto.CompanyResponse;
import za.co.pacifish.registration_service.dto.CreateCompanyRequest;
import za.co.pacifish.registration_service.dto.FirebaseUserDetailsDto;
import za.co.pacifish.registration_service.service.CompanyService;

import java.util.Objects;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> addCompany(
        @RequestBody CreateCompanyRequest request, Authentication authentication) {
        var user = (FirebaseUserDetailsDto) authentication.getPrincipal();

        if (Objects.isNull(user)) {
            throw new IllegalStateException("Authentication principal is null");
        }

        return companyService.addCompany(request, user.firebaseUid())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
