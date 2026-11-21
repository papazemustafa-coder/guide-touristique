package sn.uasz.guidetouristique.dto;

import java.time.Instant;
import sn.uasz.guidetouristique.domain.StatutConvocation;

public record ConvocationDto(Long id, Long candidatureId, Instant dateHeure, String lieu,
                             String instructions, Instant dateLimiteConfirmation,
                             StatutConvocation statut) {
}
