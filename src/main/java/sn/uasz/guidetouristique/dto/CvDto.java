package sn.uasz.guidetouristique.dto;

import java.time.Instant;
import java.util.List;

public record CvDto(Long guideId, int version, String titreProfessionnel, String profilProfessionnel,
                    List<FormationDto> formations, List<ExperienceDto> experiences,
                    List<LangueDto> langues, List<String> competences,
                    List<CertificationDto> certifications, List<String> referencesProfessionnelles,
                    Instant dateMiseAJour) {
}
