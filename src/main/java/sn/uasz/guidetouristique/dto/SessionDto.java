package sn.uasz.guidetouristique.dto;

import java.time.Instant;
import java.util.List;
import sn.uasz.guidetouristique.domain.StatutSession;

public record SessionDto(Long id, String intitule, String region, String lieu, Instant dateDebut,
                         Instant dateFin, int capacite, String description, List<String> epreuves,
                         StatutSession statut) {
}
