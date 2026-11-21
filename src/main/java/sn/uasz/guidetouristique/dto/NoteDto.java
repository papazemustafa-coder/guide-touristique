package sn.uasz.guidetouristique.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NoteDto(
        @NotBlank String critere,
        @NotNull @DecimalMin("0") BigDecimal note,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal noteMaximale) {
}
