package sn.uasz.guidetouristique.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * CVGuideSaisie : formations, experiences et langues sont obligatoires ; un CV sans aucune
 * langue est refuse (400).
 */
public record CvSaisieDto(
        @Size(max = 200) String titreProfessionnel,
        @Size(max = 3000) String profilProfessionnel,
        @NotNull @Valid List<FormationDto> formations,
        @NotNull @Valid List<ExperienceDto> experiences,
        @NotEmpty(message = "Le CV doit contenir au moins une langue.") @Valid List<LangueDto> langues,
        List<String> competences,
        @Valid List<CertificationDto> certifications,
        List<String> referencesProfessionnelles) {

    public CvSaisieDto {
        competences = competences == null ? List.of() : competences;
        certifications = certifications == null ? List.of() : certifications;
        referencesProfessionnelles = referencesProfessionnelles == null ? List.of() : referencesProfessionnelles;
    }
}
