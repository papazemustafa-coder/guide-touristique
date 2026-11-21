package sn.uasz.guidetouristique.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.uasz.guidetouristique.domain.ConvocationEntity;
import sn.uasz.guidetouristique.dto.ConvocationCreationDto;
import sn.uasz.guidetouristique.dto.ConvocationDto;

@Mapper(componentModel = "spring")
public interface ConvocationMapper {

    @Mapping(target = "candidatureId", source = "candidature.id")
    ConvocationDto toDto(ConvocationEntity convocation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidature", ignore = true)
    @Mapping(target = "statut", ignore = true)
    ConvocationEntity toEntity(ConvocationCreationDto dto);
}
