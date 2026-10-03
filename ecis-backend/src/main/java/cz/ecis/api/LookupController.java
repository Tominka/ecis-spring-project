package cz.ecis.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.ecis.core.model.LovDto;
import cz.ecis.model.dto.LookupBaseDto;
import cz.ecis.service.LkpChildInfoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${openapi.ecis.base-path:/api/v1}")
@RequiredArgsConstructor
public class LookupController implements LookupApiInterface {
    
    private final LkpChildInfoService lkpChildInfoService;

    @Override
    public ResponseEntity<List<LookupBaseDto>> getAllLkpChildInfo() {
        return ResponseEntity.ok(this.lkpChildInfoService.getAll());
    }

    @Override
    public ResponseEntity<List<LovDto>> getAllLkpChildInfoLov() {
        return ResponseEntity.ok(this.lkpChildInfoService.getAllLov());
    }
}
