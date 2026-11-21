package sn.uasz.guidetouristique.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import sn.uasz.guidetouristique.domain.Decision;

public record ResultatSaisieDto(
        @NotNull Long evaluateurId,
        @NotEmpty @Valid List<NoteDto> notes,
        @NotNull Decision decision,
        @Size(max = 3000) String appreciation) {
}
