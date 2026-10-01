package cz.ecis.modules.camp.api;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.model.LovDto;
import cz.ecis.modules.camp.model.CampDto;
import cz.ecis.modules.camp.service.CampService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class CampController implements CampApi {

    private final CampService campService;

    @Override
    public ResponseEntity<List<CampDto>> getAllCamps() {
        return ResponseEntity.ok(this.campService.getAll());
    }

    @Override
    public ResponseEntity<List<LovDto>> getAllCampsLov() {
        return ResponseEntity.ok(this.campService.getAllLov());
    }

    @Override
    public ResponseEntity<CampDto> getCampById(Long id) {
        return ResponseEntity.ok(this.campService.userFindById(id));
    }

    @Override
    @PreAuthorize("hasAuthority(T(cz.ecis.core.security.EcisRoleEnum).CAMP_EDIT.getCode())")
    public ResponseEntity<CampDto> updateCampById(Long id, CampDto dto) {
        return ResponseEntity.ok(this.campService.updateById(id, dto));
    }

    @Override
    @PreAuthorize("hasAuthority(T(cz.ecis.core.security.EcisRoleEnum).CAMP_CREATE.getCode())")
    public ResponseEntity<CampDto> createCamp(CampDto dto) {
        return ResponseEntity.ok(this.campService.create(dto));
    }

}