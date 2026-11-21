package sn.uasz.guidetouristique.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import sn.uasz.guidetouristique.domain.Role;

public record TokenRequest(@NotBlank String username, @NotNull Role role) {
}
