package sn.uasz.guidetouristique.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uasz.guidetouristique.dto.ResultatDto;
import sn.uasz.guidetouristique.dto.ResultatSaisieDto;
import sn.uasz.guidetouristique.service.ResultatService;

@RestController
@RequestMapping("/api/v1/candidatures/{candidatureId}/resultats")
public class ResultatController {

    private final ResultatService service;

    public ResultatController(ResultatService service) {
        this.service = service;
    }

    /** Operation reservee a un evaluateur habilite. */
    @PostMapping
    @PreAuthorize("hasRole('EVALUATEUR')")
    public ResponseEntity<ResultatDto> enregistrer(@PathVariable @Min(1) Long candidatureId,
                                                   @Valid @RequestBody ResultatSaisieDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.enregistrer(candidatureId, dto));
    }

    @GetMapping
    public ResultatDto consulter(@PathVariable @Min(1) Long candidatureId) {
        return service.consulter(candidatureId);
    }

    /** Publication du resultat (operation ajoutee, absente du contrat). */
    @PostMapping("/publication")
    @PreAuthorize("hasAnyRole('EVALUATEUR','ADMIN')")
    public ResultatDto publier(@PathVariable @Min(1) Long candidatureId) {
        return service.publier(candidatureId);
    }
}
