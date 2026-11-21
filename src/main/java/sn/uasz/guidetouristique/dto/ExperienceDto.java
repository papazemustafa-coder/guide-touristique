package sn.uasz.guidetouristique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExperienceDto(
        @NotBlank String poste,
        @NotBlank String organisme,
        @NotNull LocalDate dateDebut,
        LocalDate dateFin,
        boolean enCours,
        @Size(max = 3000) String description,
        String region) {
}
