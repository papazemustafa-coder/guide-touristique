package sn.uasz.guidetouristique.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.uasz.guidetouristique.domain.CandidatureEntity;
import sn.uasz.guidetouristique.dto.CandidatureDto;

@Mapper(componentModel = "spring")
public interface CandidatureMapper {

    @Mapping(target = "guideId", source = "guide.id")
    @Mapping(target = "sessionId", source = "session.id")
    CandidatureDto toDto(CandidatureEntity candidature);
}
