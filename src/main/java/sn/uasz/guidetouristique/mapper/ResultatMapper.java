package sn.uasz.guidetouristique.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sn.uasz.guidetouristique.domain.NoteEmb;
import sn.uasz.guidetouristique.domain.ResultatEntity;
import sn.uasz.guidetouristique.dto.NoteDto;
import sn.uasz.guidetouristique.dto.ResultatDto;

@Mapper(componentModel = "spring")
public interface ResultatMapper {

    @Mapping(target = "candidatureId", source = "candidature.id")
    ResultatDto toDto(ResultatEntity resultat);

    NoteDto noteToDto(NoteEmb e);
    NoteEmb noteToEmb(NoteDto d);
}
