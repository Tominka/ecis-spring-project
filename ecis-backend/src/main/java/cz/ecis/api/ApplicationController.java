package cz.ecis.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.security.annotation.CampSecured;
import cz.ecis.model.dto.ApplicationCreateDto;
import cz.ecis.model.dto.ApplicationDto;
import cz.ecis.model.dto.ApplicationOverviewDto;
import cz.ecis.service.ApplicationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class ApplicationController implements ApplicationApiInterface {

    private final ApplicationService applicationService;

    @Override
    @CampSecured
    public ResponseEntity<List<ApplicationOverviewDto>> getAllApplications() {
        return ResponseEntity.ok(
            this.applicationService.getAllApplications()
        );
    }

    @Override
    @CampSecured
    public ResponseEntity<ApplicationDto> getApplicationById(Long id) {
        return ResponseEntity.ok(
            this.applicationService.getApplicationById(id)
        );
    }

    @Override
    @CampSecured
    public ResponseEntity<ApplicationDto> createApplication(ApplicationCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            this.applicationService.createApplication(dto)
        );
    }
}
