package cz.ecis.modules.child.api;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.model.LovDto;
import cz.ecis.core.security.EcisRoleEnum;
import cz.ecis.core.security.annotation.CampSecured;
import cz.ecis.modules.child.model.ChildDto;
import cz.ecis.modules.child.service.ChildService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class ChildController implements ChildApi {

    private final ChildService childService;

    @Override
    @CampSecured(roles = {EcisRoleEnum.CHILDREN_READ})
    public ResponseEntity<List<ChildDto>> getAllChildren() {
        return ResponseEntity.ok(this.childService.getAll());
    }

    @Override
    @CampSecured(roles = {EcisRoleEnum.CHILDREN_READ})
    public ResponseEntity<List<LovDto>> getAllChildrenLov() {
        return ResponseEntity.ok(this.childService.getAllLov());
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