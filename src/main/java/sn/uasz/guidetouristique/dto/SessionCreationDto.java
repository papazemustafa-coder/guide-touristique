package sn.uasz.guidetouristique.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record SessionCreationDto(
        @NotBlank String intitule,
        @NotBlank String region,
        String lieu,
        @NotNull Instant dateDebut,
        @NotNull Instant dateFin,
        @NotNull @Min(1) Integer capacite,
        String description,
        List<String> epreuves) {

    public SessionCreationDto {
        epreuves = epreuves == null ? List.of() : epreuves;
    }
}
