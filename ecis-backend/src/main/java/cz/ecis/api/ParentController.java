package cz.ecis.api;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.model.LovDto;
import cz.ecis.core.security.EcisRoleEnum;
import cz.ecis.core.security.annotation.CampSecured;
import cz.ecis.model.dto.ParentDto;
import cz.ecis.service.ParentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class ParentController implements ParentApiInterface {

    private final ParentService parentService;

    @Override
    @CampSecured //(roles = {EcisRoleEnum.PARENTS_READ})
    public ResponseEntity<List<ParentDto>> getAllParents() {
        return ResponseEntity.ok(this.parentService.getAll());
    }

    @Override
    @CampSecured //(roles = {EcisRoleEnum.PARENTS_READ})
    public ResponseEntity<List<LovDto>> getAllParentsLov() {
        return ResponseEntity.ok(this.parentService.getAllLov());
    }

    // @Override
    // public ResponseEntity<CampDto> getCampById(Long id) {
    //     return ResponseEntity.ok(this.campService.userFindById(id));
    // }

    // @Override
    // @PreAuthorize("hasAuthority(T(cz.ecis.core.security.EcisRoleEnum).CAMP_EDIT.getCode())")
    // public ResponseEntity<CampDto> updateCampById(Long id, CampDto dto) {
    //     return ResponseEntity.ok(this.campService.updateById(id, dto));
    // }

    // @Override
    // @PreAuthorize("hasAuthority(T(cz.ecis.core.security.EcisRoleEnum).CAMP_CREATE.getCode())")
    // public ResponseEntity<CampDto> createCamp(CampDto dto) {
    //     return ResponseEntity.ok(this.campService.create(dto));
    // }

}