package sn.uasz.guidetouristique.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.uasz.guidetouristique.domain.CertificationEmb;
import sn.uasz.guidetouristique.domain.CvEntity;
import sn.uasz.guidetouristique.domain.ExperienceEmb;
import sn.uasz.guidetouristique.domain.FormationEmb;
import sn.uasz.guidetouristique.domain.LangueEmb;
import sn.uasz.guidetouristique.dto.CertificationDto;
import sn.uasz.guidetouristique.dto.CvDto;
import sn.uasz.guidetouristique.dto.CvSaisieDto;
import sn.uasz.guidetouristique.dto.ExperienceDto;
import sn.uasz.guidetouristique.dto.FormationDto;
import sn.uasz.guidetouristique.dto.LangueDto;

@Mapper(componentModel = "spring")
public interface CvMapper {

    @Mapping(target = "guideId", source = "guide.id")
    CvDto toDto(CvEntity cv);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "guide", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "dateMiseAJour", ignore = true)
    void appliquer(CvSaisieDto dto, @MappingTarget CvEntity cv);

    FormationDto formationToDto(FormationEmb e);
    FormationEmb formationToEmb(FormationDto d);

    ExperienceDto experienceToDto(ExperienceEmb e);
    ExperienceEmb experienceToEmb(ExperienceDto d);

    LangueDto langueToDto(LangueEmb e);
    LangueEmb langueToEmb(LangueDto d);

    CertificationDto certificationToDto(CertificationEmb e);
    CertificationEmb certificationToEmb(CertificationDto d);
}
