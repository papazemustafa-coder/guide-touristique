package sn.uasz.guidetouristique.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.uasz.guidetouristique.domain.SessionEntity;
import sn.uasz.guidetouristique.dto.SessionCreationDto;
import sn.uasz.guidetouristique.dto.SessionDto;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    SessionDto toDto(SessionEntity session);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    SessionEntity toEntity(SessionCreationDto dto);
}
