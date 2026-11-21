package sn.uasz.guidetouristique.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.uasz.guidetouristique.domain.SessionEntity;
import sn.uasz.guidetouristique.domain.StatutSession;
import sn.uasz.guidetouristique.dto.SessionCreationDto;
import sn.uasz.guidetouristique.dto.SessionDto;
import sn.uasz.guidetouristique.erreur.BadRequestException;
import sn.uasz.guidetouristique.erreur.ConflictException;
import sn.uasz.guidetouristique.mapper.SessionMapper;
import sn.uasz.guidetouristique.repository.SessionRepository;
import sn.uasz.guidetouristique.repository.SessionSpecifications;

@Service
@Transactional
public class SessionService {

    private final SessionRepository repository;
    private final SessionMapper mapper;

    public SessionService(SessionRepository repository, SessionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /** dateFin posterieure a dateDebut, capacite >= 1 (valide par bean validation). */
    public SessionDto creer(SessionCreationDto dto) {
        if (!dto.dateFin().isAfter(dto.dateDebut())) {
            throw new BadRequestException("dateFin doit être postérieure à dateDebut.");
        }
        if (repository.existsByIntituleIgnoreCaseAndRegionIgnoreCaseAndDateDebut(
                dto.intitule(), dto.region(), dto.dateDebut())) {
            throw new ConflictException("Une session identique existe déjà.");
        }
        SessionEntity session = mapper.toEntity(dto);
        session.setStatut(StatutSession.OUVERTE); // hypothese : une session creee est ouverte aux candidatures
        return mapper.toDto(repository.save(session));
    }

    @Transactional(readOnly = true)
    public List<SessionDto> rechercher(String region, StatutSession statut) {
        return repository.findAll(SessionSpecifications.recherche(region, statut), Sort.by("id"))
                .stream().map(mapper::toDto).toList();
    }
}
