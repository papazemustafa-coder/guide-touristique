package sn.uasz.guidetouristique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sn.uasz.guidetouristique.domain.NiveauLangue;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LangueDto(@NotBlank String langue, @NotNull NiveauLangue niveau) {
}
