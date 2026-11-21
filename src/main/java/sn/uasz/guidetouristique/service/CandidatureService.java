package sn.uasz.guidetouristique.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sn.uasz.guidetouristique.domain.CandidatureEntity;
import sn.uasz.guidetouristique.domain.ConvocationEntity;
import sn.uasz.guidetouristique.domain.CvEntity;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.SessionEntity;
import sn.uasz.guidetouristique.domain.StatutCandidature;
import sn.uasz.guidetouristique.domain.StatutConvocation;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.domain.StatutSession;
import sn.uasz.guidetouristique.dto.CandidatureCreationDto;
import sn.uasz.guidetouristique.dto.CandidatureDto;
import sn.uasz.guidetouristique.dto.ConvocationCreationDto;
import sn.uasz.guidetouristique.dto.ConvocationDto;
import sn.uasz.guidetouristique.erreur.BadRequestException;
import sn.uasz.guidetouristique.erreur.ConflictException;
import sn.uasz.guidetouristique.erreur.ResourceNotFoundException;
import sn.uasz.guidetouristique.mapper.CandidatureMapper;
import sn.uasz.guidetouristique.mapper.ConvocationMapper;
import sn.uasz.guidetouristique.mapper.CvMapper;
import sn.uasz.guidetouristique.repository.CandidatureRepository;
import sn.uasz.guidetouristique.repository.ConvocationRepository;
import sn.uasz.guidetouristique.repository.CvRepository;
import sn.uasz.guidetouristique.repository.SessionRepository;
import sn.uasz.guidetouristique.security.GuideAccessPolicy;

@Service
@Transactional
public class CandidatureService {

    private final CandidatureRepository candidatures;
    private final SessionRepository sessions;
    private final CvRepository cvs;
    private final ConvocationRepository convocations;
    private final GuideService guideService;
    private final GuideAccessPolicy policy;
    private final CandidatureMapper mapper;
    private final ConvocationMapper convocationMapper;
    private final CvMapper cvMapper;
    private final ObjectMapper json;

    public CandidatureService(CandidatureRepository candidatures, SessionRepository sessions,
                              CvRepository cvs, ConvocationRepository convocations,
                              GuideService guideService, GuideAccessPolicy policy,
                              CandidatureMapper mapper, ConvocationMapper convocationMapper,
                              CvMapper cvMapper, ObjectMapper json) {
        this.candidatures = candidatures;
        this.sessions = sessions;
        this.cvs = cvs;
        this.convocations = convocations;
        this.guideService = guideService;
        this.policy = policy;
        this.mapper = mapper;
        this.convocationMapper = convocationMapper;
        this.cvMapper = cvMapper;
        this.json = json;
    }

    /** Charge la candidature ou leve 404. Reutilise par ResultatService. */
    @Transactional(readOnly = true)
    public CandidatureEntity charger(Long id) {
        return candidatures.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune candidature trouvée pour cet identifiant."));
    }

    /**
     * Soumission : verifie la completude du profil et du CV, les regles de session
     * (ouverte, non complete, unicite), fige une copie du CV (versionCV) et fait
     * evoluer le statut du guide.
     */
    public CandidatureDto soumettre(Long guideId, CandidatureCreationDto dto) {
        GuideEntity guide = guideService.charger(guideId);
        policy.verifierEcritureCv(guide);

        SessionEntity session = sessions.findById(dto.sessionId())
                .orElseThrow(() -> new ResourceNotFoundException("Aucune session trouvée pour cet identifiant."));

        // Completude du profil et du CV -> 400 « dossier incomplet »
        List<String> manques = new ArrayList<>();
        if (!StringUtils.hasText(guide.getTelephone())) {
            manques.add("téléphone du profil");
        }
        CvEntity cv = cvs.findByGuideId(guideId).orElse(null);
        if (cv == null) {
            manques.add("CV");
        } else if (cv.getLangues().isEmpty()) {
            manques.add("langue du CV");
        }
        if (!manques.isEmpty()) {
            throw new BadRequestException("Dossier incomplet : " + String.join(", ", manques) + ".");
        }

        // Regles de session -> 409
        if (session.getStatut() != StatutSession.OUVERTE) {
            throw new ConflictException("La session n'est pas ouverte aux candidatures.");
        }
        if (candidatures.existsByGuideIdAndSessionId(guideId, session.getId())) {
            throw new ConflictException("Le guide a déjà une candidature active sur cette session.");
        }
        if (candidatures.countBySessionId(session.getId()) >= session.getCapacite()) {
            throw new ConflictException("La session est complète.");
        }

        CandidatureEntity c = new CandidatureEntity();
        c.setGuide(guide);
        c.setSession(session);
        c.setVersionCV(cv.getVersion());
        c.setCvFige(figer(cv));
        c.setStatut(StatutCandidature.SOUMISE); // recevabilite consideree acquise a la soumission
        c.setDateSoumission(Instant.now());
        c.setCommentaire(dto.commentaire());
        c = candidatures.save(c);

        if (guide.getStatut() == StatutGuide.BROUILLON) {
            guide.setStatut(StatutGuide.DOSSIER_SOUMIS);
        }
        return mapper.toDto(c);
    }

    @Transactional(readOnly = true)
    public List<CandidatureDto> listerParGuide(Long guideId) {
        GuideEntity guide = guideService.charger(guideId);
        policy.verifierLecture(guide);
        return candidatures.findByGuideIdOrderByIdAsc(guideId).stream().map(mapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CandidatureDto consulter(Long id) {
        CandidatureEntity c = charger(id);
        policy.verifierLecture(c.getGuide());
        return mapper.toDto(c);
    }

    /** Convocation : uniquement si SOUMISE (409 sinon). Candidature -> CONVOQUEE, guide -> EN_EVALUATION. */
    public ConvocationDto convoquer(Long candidatureId, ConvocationCreationDto dto) {
        CandidatureEntity c = charger(candidatureId);
        if (c.getStatut() != StatutCandidature.SOUMISE) {
            throw new ConflictException("La candidature doit être au statut SOUMISE pour être convoquée.");
        }
        ConvocationEntity convocation = convocationMapper.toEntity(dto);
        convocation.setCandidature(c);
        convocation.setStatut(StatutConvocation.ENVOYEE);
        convocation = convocations.save(convocation);

        c.setStatut(StatutCandidature.CONVOQUEE);
        c.getGuide().setStatut(StatutGuide.EN_EVALUATION);
        return convocationMapper.toDto(convocation);
    }

    private String figer(CvEntity cv) {
        try {
            return json.writeValueAsString(cvMapper.toDto(cv));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Impossible de figer le CV", e);
        }
    }
}
