package sn.uasz.guidetouristique;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.SessionEntity;
import sn.uasz.guidetouristique.domain.StatutCandidature;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.domain.StatutSession;
import sn.uasz.guidetouristique.repository.CandidatureRepository;
import sn.uasz.guidetouristique.repository.GuideRepository;
import sn.uasz.guidetouristique.repository.SessionRepository;

/** Acces direct a la base pour les tests (etats que l'API du contrat ne permet pas d'atteindre). */
@Component
public class TestSupport {

    private final GuideRepository guides;
    private final SessionRepository sessions;
    private final CandidatureRepository candidatures;

    public TestSupport(GuideRepository guides, SessionRepository sessions, CandidatureRepository candidatures) {
        this.guides = guides;
        this.sessions = sessions;
        this.candidatures = candidatures;
    }

    @Transactional
    public void definirStatutGuide(long id, StatutGuide statut) {
        GuideEntity g = guides.findById(id).orElseThrow();
        g.setStatut(statut);
    }

    @Transactional(readOnly = true)
    public StatutGuide lireStatutGuide(long id) {
        return guides.findById(id).orElseThrow().getStatut();
    }

    @Transactional
    public void definirStatutSession(long id, StatutSession statut) {
        SessionEntity s = sessions.findById(id).orElseThrow();
        s.setStatut(statut);
    }

    @Transactional(readOnly = true)
    public StatutCandidature lireStatutCandidature(long id) {
        return candidatures.findById(id).orElseThrow().getStatut();
    }

    /** Copie figee du CV (JSON) rattachee a la candidature : non exposee par le contrat. */
    @Transactional(readOnly = true)
    public String lireCvFige(long candidatureId) {
        return candidatures.findById(candidatureId).orElseThrow().getCvFige();
    }
}
