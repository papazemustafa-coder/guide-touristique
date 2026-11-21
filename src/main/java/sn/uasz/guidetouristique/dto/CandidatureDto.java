package sn.uasz.guidetouristique.dto;

import java.time.Instant;
import sn.uasz.guidetouristique.domain.StatutCandidature;

public record CandidatureDto(Long id, Long guideId, Long sessionId, int versionCV,
                             StatutCandidature statut, Instant dateSoumission, String commentaire) {
}
