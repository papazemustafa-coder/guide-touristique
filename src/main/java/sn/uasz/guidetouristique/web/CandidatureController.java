package sn.uasz.guidetouristique.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uasz.guidetouristique.dto.CandidatureCreationDto;
import sn.uasz.guidetouristique.dto.CandidatureDto;
import sn.uasz.guidetouristique.dto.ConvocationCreationDto;
import sn.uasz.guidetouristique.dto.ConvocationDto;
import sn.uasz.guidetouristique.service.CandidatureService;

@RestController
@RequestMapping("/api/v1")
public class CandidatureController {

    private final CandidatureService service;

    public CandidatureController(CandidatureService service) {
        this.service = service;
    }

    @PostMapping("/guides/{guideId}/candidatures")
    public ResponseEntity<CandidatureDto> soumettre(@PathVariable @Min(1) Long guideId,
                                                    @Valid @RequestBody CandidatureCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.soumettre(guideId, dto));
    }

    @GetMapping("/guides/{guideId}/candidatures")
    public List<CandidatureDto> lister(@PathVariable @Min(1) Long guideId) {
        return service.listerParGuide(guideId);
    }

    @GetMapping("/candidatures/{candidatureId}")
    public CandidatureDto consulter(@PathVariable @Min(1) Long candidatureId) {
        return service.consulter(candidatureId);
    }

    /** Operation reservee a un gestionnaire habilite (ADMIN accepte). */
    @PostMapping("/candidatures/{candidatureId}/convocation")
    @PreAuthorize("hasAnyRole('GESTIONNAIRE','ADMIN')")
    public ConvocationDto convoquer(@PathVariable @Min(1) Long candidatureId,
                                    @Valid @RequestBody ConvocationCreationDto dto) {
        return service.convoquer(candidatureId, dto);
    }
}
