package sn.uasz.guidetouristique.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/** GuideCreation et GuideModification (schemas identiques, additionalProperties: false). */
public record GuideSaisieDto(
        @NotBlank @Size(max = 100) String prenom,
        @NotBlank @Size(max = 100) String nom,
        @NotBlank @Email String email,
        String telephone,
        @NotBlank String region,
        String ville,
        String adresseProfessionnelle,
        List<String> specialites) {

    public GuideSaisieDto {
        specialites = specialites == null ? List.of() : specialites;
    }
}
