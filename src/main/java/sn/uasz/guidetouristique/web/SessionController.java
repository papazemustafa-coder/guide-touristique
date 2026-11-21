package sn.uasz.guidetouristique.web;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sn.uasz.guidetouristique.domain.StatutSession;
import sn.uasz.guidetouristique.dto.SessionCreationDto;
import sn.uasz.guidetouristique.dto.SessionDto;
import sn.uasz.guidetouristique.service.SessionService;

@RestController
@RequestMapping("/api/v1/sessions-evaluation")
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @GetMapping
    public List<SessionDto> rechercher(@RequestParam(required = false) String region,
                                       @RequestParam(required = false) StatutSession statut) {
        return service.rechercher(region, statut);
    }

    /** Operation reservee a un administrateur habilite. */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SessionDto> creer(@Valid @RequestBody SessionCreationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.creer(dto));
    }
}
