package sn.uasz.guidetouristique.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** confirmationExactitude : le contrat impose la valeur true (const). */
public record CandidatureCreationDto(
        @NotNull Long sessionId,
        @NotNull @AssertTrue(message = "La confirmation d'exactitude doit être true.") Boolean confirmationExactitude,
        @Size(max = 2000) String commentaire) {
}
