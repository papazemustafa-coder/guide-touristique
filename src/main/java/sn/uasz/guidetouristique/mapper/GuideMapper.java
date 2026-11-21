package sn.uasz.guidetouristique.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.dto.GuideDto;
import sn.uasz.guidetouristique.dto.GuideSaisieDto;

@Mapper(componentModel = "spring")
public interface GuideMapper {

    GuideDto toDto(GuideEntity guide);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "proprietaire", ignore = true)
    @Mapping(target = "cv", ignore = true)
    GuideEntity toEntity(GuideSaisieDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "proprietaire", ignore = true)
    @Mapping(target = "cv", ignore = true)
    void update(GuideSaisieDto dto, @MappingTarget GuideEntity guide);
}
