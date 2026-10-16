package cz.ecis.api;

import cz.ecis.service.UserService;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.model.LovDto;
import cz.ecis.model.dto.CampDto;
import cz.ecis.model.dto.UserCampChangeDto;
import cz.ecis.model.dto.UserSettingsDto;
import cz.ecis.service.CampService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class UserController implements UserApiInterface {

    private final UserService userService;
    private final CampService campService;

    @Override
    public ResponseEntity<List<CampDto>> getUserCamps() {
        return ResponseEntity.ok(this.campService.getUserCamps());
    }

    @Override
    public ResponseEntity<List<LovDto>> getUserCampsLov() {
        return ResponseEntity.ok(this.campService.getUserCampsLov());
    }

    @Override
    public ResponseEntity<Void> selectUserCamp(UserCampChangeDto dto) {
        this.userService.selectCamp(dto);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Object> getUserDuties() {
        return ResponseEntity.ok(new Object());
    }

    @Override
    public ResponseEntity<UserSettingsDto> updateUserSettings(Long id, UserSettingsDto dto) {
        return ResponseEntity.ok(
            this.userService.updateUserSettings(id, dto)
        );
    }
}
