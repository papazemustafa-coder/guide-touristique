package sn.uasz.guidetouristique.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.dto.GuideDto;
import sn.uasz.guidetouristique.dto.GuideSaisieDto;
import sn.uasz.guidetouristique.erreur.ConflictException;
import sn.uasz.guidetouristique.erreur.ResourceNotFoundException;
import sn.uasz.guidetouristique.mapper.GuideMapper;
import sn.uasz.guidetouristique.repository.GuideRepository;
import sn.uasz.guidetouristique.repository.GuideSpecifications;
import sn.uasz.guidetouristique.security.CurrentUser;
import sn.uasz.guidetouristique.security.GuideAccessPolicy;

@Service
@Transactional
public class GuideService {

    private final GuideRepository repository;
    private final GuideMapper mapper;
    private final GuideAccessPolicy policy;
    private final CurrentUser user;

    public GuideService(GuideRepository repository, GuideMapper mapper,
                        GuideAccessPolicy policy, CurrentUser user) {
        this.repository = repository;
        this.mapper = mapper;
        this.policy = policy;
        this.user = user;
    }

    /** Charge l'entite ou leve 404. Reutilise par les autres services. */
    @Transactional(readOnly = true)
    public GuideEntity charger(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun guide trouvé pour cet identifiant."));
    }

    public GuideDto creer(GuideSaisieDto dto) {
        if (repository.existsByEmailIgnoreCase(dto.email())) {
            throw new ConflictException("Un guide existe déjà avec cet email.");
        }
        GuideEntity guide = mapper.toEntity(dto);
        guide.setStatut(StatutGuide.BROUILLON);
        guide.setProprietaire(user.username());
        return mapper.toDto(repository.save(guide));
    }

    @Transactional(readOnly = true)
    public GuideDto consulter(Long id) {
        GuideEntity guide = charger(id);
        policy.verifierLecture(guide);
        return mapper.toDto(guide);
    }

    public GuideDto modifier(Long id, GuideSaisieDto dto) {
        GuideEntity guide = charger(id);
        policy.verifierEcriture(guide);
        if (repository.existsByEmailIgnoreCaseAndIdNot(dto.email(), id)) {
            throw new ConflictException("Un autre guide utilise déjà cet email.");
        }
        mapper.update(dto, guide);
        return mapper.toDto(repository.save(guide));
    }

    public void supprimer(Long id) {
        GuideEntity guide = charger(id);
        policy.verifierEcriture(guide);
        if (guide.getStatut() != StatutGuide.BROUILLON) {
            throw new ConflictException("Le dossier est soumis : suppression impossible.");
        }
        repository.delete(guide);
    }

    /** Le contrat renvoie un tableau simple ; la page est pilotee par les parametres page et taille. */
    @Transactional(readOnly = true)
    public Page<GuideDto> rechercher(String nom, String langue, String region,
                                     StatutGuide statut, int page, int taille) {
        return repository.findAll(GuideSpecifications.recherche(nom, langue, region, statut),
                PageRequest.of(page, taille, Sort.by("id"))).map(mapper::toDto);
    }
}
