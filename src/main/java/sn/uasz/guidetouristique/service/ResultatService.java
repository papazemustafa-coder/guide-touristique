package sn.uasz.guidetouristique.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.uasz.guidetouristique.domain.CandidatureEntity;
import sn.uasz.guidetouristique.domain.Decision;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.ResultatEntity;
import sn.uasz.guidetouristique.domain.StatutCandidature;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.domain.StatutPublication;
import sn.uasz.guidetouristique.dto.NoteDto;
import sn.uasz.guidetouristique.dto.ResultatDto;
import sn.uasz.guidetouristique.dto.ResultatSaisieDto;
import sn.uasz.guidetouristique.erreur.BadRequestException;
import sn.uasz.guidetouristique.erreur.ConflictException;
import sn.uasz.guidetouristique.erreur.ResourceNotFoundException;
import sn.uasz.guidetouristique.mapper.ResultatMapper;
import sn.uasz.guidetouristique.repository.ResultatRepository;
import sn.uasz.guidetouristique.security.GuideAccessPolicy;
import sn.uasz.guidetouristique.security.ResultatProperties;

@Service
@Transactional
public class ResultatService {

    private static final BigDecimal VINGT = BigDecimal.valueOf(20);

    private final ResultatRepository resultats;
    private final CandidatureService candidatureService;
    private final GuideAccessPolicy policy;
    private final ResultatMapper mapper;
    private final ResultatProperties props;

    public ResultatService(ResultatRepository resultats, CandidatureService candidatureService,
                           GuideAccessPolicy policy, ResultatMapper mapper, ResultatProperties props) {
        this.resultats = resultats;
        this.candidatureService = candidatureService;
        this.policy = policy;
        this.mapper = mapper;
        this.props = props;
    }

    /** noteTotale = (somme note / somme noteMaximale) x 20, arrondie a 2 decimales. */
    static BigDecimal calculerNoteTotale(Iterable<NoteDto> notes) {
        BigDecimal somme = BigDecimal.ZERO;
        BigDecimal sommeMax = BigDecimal.ZERO;
        for (NoteDto n : notes) {
            somme = somme.add(n.note());
            sommeMax = sommeMax.add(n.noteMaximale());
        }
        return somme.multiply(VINGT).divide(sommeMax, 2, RoundingMode.HALF_UP);
    }

    public ResultatDto enregistrer(Long candidatureId, ResultatSaisieDto dto) {
        CandidatureEntity c = candidatureService.charger(candidatureId);

        // Controles de coherence -> 400
        for (NoteDto n : dto.notes()) {
            if (n.note().compareTo(n.noteMaximale()) > 0) {
                throw new BadRequestException("La note du critère « " + n.critere()
                        + " » dépasse la note maximale.");
            }
        }
        BigDecimal noteTotale = calculerNoteTotale(dto.notes());
        if (dto.decision() == Decision.ADMIS && noteTotale.compareTo(props.seuilAdmission()) < 0) {
            throw new BadRequestException("Décision ADMIS refusée : la note totale (" + noteTotale
                    + ") est inférieure au seuil d'admission (" + props.seuilAdmission() + ").");
        }

        // Etat de la candidature / unicite du resultat -> 409
        if (c.getStatut() != StatutCandidature.CONVOQUEE) {
            throw new ConflictException("La candidature doit être au statut CONVOQUEE pour recevoir un résultat.");
        }
        if (resultats.findByCandidatureId(candidatureId).isPresent()) {
            throw new ConflictException("Un résultat existe déjà pour cette candidature.");
        }

        ResultatEntity r = new ResultatEntity();
        r.setCandidature(c);
        r.setEvaluateurId(dto.evaluateurId());
        r.setNotes(dto.notes().stream().map(mapper::noteToEmb).collect(java.util.stream.Collectors.toList()));
        r.setDecision(dto.decision());
        r.setAppreciation(dto.appreciation());
        r.setDateEvaluation(Instant.now());
        r.setNoteTotale(noteTotale);
        r.setStatutPublication(StatutPublication.BROUILLON);
        r = resultats.save(r);

        // Mise a jour des statuts de la candidature et du guide
        GuideEntity guide = c.getGuide();
        switch (dto.decision()) {
            case ADMIS -> {
                c.setStatut(StatutCandidature.ADMISE);
                guide.setStatut(StatutGuide.ADMIS);
            }
            case NON_ADMIS -> {
                c.setStatut(StatutCandidature.NON_ADMISE);
                guide.setStatut(StatutGuide.NON_ADMIS);
            }
            case AJOURNE -> {
                c.setStatut(StatutCandidature.EVALUEE);
                guide.setStatut(StatutGuide.EN_EVALUATION);
            }
        }
        return mapper.toDto(r);
    }

    /** EVALUATEUR et ADMIN voient tout ; un candidat ne voit que son resultat, une fois PUBLIE. */
    @Transactional(readOnly = true)
    public ResultatDto consulter(Long candidatureId) {
        CandidatureEntity c = candidatureService.charger(candidatureId);
        boolean complet = policy.accesCompletResultat();
        if (!complet) {
            policy.verifierProprietaire(c.getGuide());
        }
        ResultatEntity r = resultats.findByCandidatureId(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun résultat disponible pour cette candidature."));
        if (!complet && r.getStatutPublication() != StatutPublication.PUBLIE) {
            throw new ResourceNotFoundException("Aucun résultat disponible pour cette candidature.");
        }
        return mapper.toDto(r);
    }

    /** Operation de publication : BROUILLON -> PUBLIE (hors contrat, requise par le sujet). */
    public ResultatDto publier(Long candidatureId) {
        candidatureService.charger(candidatureId);
        ResultatEntity r = resultats.findByCandidatureId(candidatureId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun résultat à publier pour cette candidature."));
        if (r.getStatutPublication() == StatutPublication.PUBLIE) {
            throw new ConflictException("Le résultat est déjà publié.");
        }
        r.setStatutPublication(StatutPublication.PUBLIE);
        return mapper.toDto(r);
    }
}
