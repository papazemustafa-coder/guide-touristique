package sn.uasz.guidetouristique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConvocationCreationDto(
        @NotNull Instant dateHeure,
        @NotBlank String lieu,
        String instructions,
        Instant dateLimiteConfirmation) {
}
