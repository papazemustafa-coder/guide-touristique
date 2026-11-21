package sn.uasz.guidetouristique.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import sn.uasz.guidetouristique.domain.Decision;
import sn.uasz.guidetouristique.domain.StatutPublication;

public record ResultatDto(Long id, Long candidatureId, Long evaluateurId, List<NoteDto> notes,
                          Decision decision, String appreciation, Instant dateEvaluation,
                          BigDecimal noteTotale, StatutPublication statutPublication) {
}
