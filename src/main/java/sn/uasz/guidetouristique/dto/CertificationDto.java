package sn.uasz.guidetouristique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CertificationDto(
        @NotBlank String intitule,
        @NotBlank String organisme,
        LocalDate dateObtention,
        String reference) {
}
