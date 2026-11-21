package sn.uasz.guidetouristique.dto;

import java.util.List;
import sn.uasz.guidetouristique.domain.StatutGuide;

public record GuideDto(Long id, String prenom, String nom, String email, String telephone,
                       String photoUrl, String region, String ville, String adresseProfessionnelle,
                       List<String> specialites, StatutGuide statut) {
}
