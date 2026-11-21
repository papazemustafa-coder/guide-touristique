package sn.uasz.guidetouristique.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sn.uasz.guidetouristique.dto.CvDto;
import sn.uasz.guidetouristique.dto.CvSaisieDto;
import sn.uasz.guidetouristique.service.CvService;

@RestController
@RequestMapping("/api/v1/guides/{guideId}/cv")
public class CvController {

    private final CvService service;

    public CvController(CvService service) {
        this.service = service;
    }

    @GetMapping
    public CvDto consulter(@PathVariable @Min(1) Long guideId) {
        return service.consulter(guideId);
    }

    @PutMapping
    public CvDto enregistrer(@PathVariable @Min(1) Long guideId, @Valid @RequestBody CvSaisieDto dto) {
        return service.enregistrer(guideId, dto);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exporter(@PathVariable @Min(1) Long guideId) {
        byte[] contenu = service.exporter(guideId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("cv-guide-" + guideId + ".pdf").build().toString())
                .body(contenu);
    }
}
