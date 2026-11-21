package sn.uasz.guidetouristique.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.dto.GuideDto;
import sn.uasz.guidetouristique.dto.GuideSaisieDto;
import sn.uasz.guidetouristique.service.GuideService;

@RestController
@RequestMapping("/api/v1/guides")
public class GuideController {

    private final GuideService service;

    public GuideController(GuideService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('GUIDE','GESTIONNAIRE','ADMIN')")
    public ResponseEntity<GuideDto> creer(@Valid @RequestBody GuideSaisieDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(dto));
    }

    /** Tableau simple (contrat) ; le total est expose dans l'en-tete X-Total-Count. */
    @GetMapping
    @PreAuthorize("hasAnyRole('GESTIONNAIRE','EVALUATEUR','ADMIN')")
    public ResponseEntity<List<GuideDto>> rechercher(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String langue,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) StatutGuide statut,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int taille) {
        Page<GuideDto> resultat = service.rechercher(nom, langue, region, statut, page, taille);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(resultat.getTotalElements()))
                .body(resultat.getContent());
    }

    @GetMapping("/{guideId}")
    public GuideDto consulter(@PathVariable @Min(1) Long guideId) {
        return service.consulter(guideId);
    }

    @PutMapping("/{guideId}")
    public GuideDto modifier(@PathVariable @Min(1) Long guideId, @Valid @RequestBody GuideSaisieDto dto) {
        return service.modifier(guideId, dto);
    }

    @DeleteMapping("/{guideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable @Min(1) Long guideId) {
        service.supprimer(guideId);
    }
}
