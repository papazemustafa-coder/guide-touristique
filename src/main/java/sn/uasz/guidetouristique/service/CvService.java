package sn.uasz.guidetouristique.service;

import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.uasz.guidetouristique.domain.CvEntity;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.dto.CvDto;
import sn.uasz.guidetouristique.dto.CvSaisieDto;
import sn.uasz.guidetouristique.erreur.ConflictException;
import sn.uasz.guidetouristique.erreur.ResourceNotFoundException;
import sn.uasz.guidetouristique.mapper.CvMapper;
import sn.uasz.guidetouristique.repository.CvRepository;
import sn.uasz.guidetouristique.security.GuideAccessPolicy;

@Service
@Transactional
public class CvService {

    private final CvRepository cvRepository;
    private final GuideService guideService;
    private final GuideAccessPolicy policy;
    private final CvMapper mapper;
    private final CvPdfGenerator pdf;

    public CvService(CvRepository cvRepository, GuideService guideService,
                     GuideAccessPolicy policy, CvMapper mapper, CvPdfGenerator pdf) {
        this.cvRepository = cvRepository;
        this.guideService = guideService;
        this.policy = policy;
        this.mapper = mapper;
        this.pdf = pdf;
    }

    /** Cree ou met a jour le CV ; la version est incrementee a chaque enregistrement. */
    public CvDto enregistrer(Long guideId, CvSaisieDto dto) {
        GuideEntity guide = guideService.charger(guideId);
        policy.verifierEcritureCv(guide);
        if (guide.getStatut() != StatutGuide.BROUILLON) {
            throw new ConflictException("Le CV ne peut plus être modifié après soumission.");
        }
        CvEntity cv = cvRepository.findByGuideId(guideId).orElseGet(() -> {
            CvEntity nouveau = new CvEntity();
            nouveau.setGuide(guide);
            nouveau.setVersion(0);
            guide.setCv(nouveau);
            return nouveau;
        });
        mapper.appliquer(dto, cv);
        cv.setVersion(cv.getVersion() + 1);
        cv.setDateMiseAJour(Instant.now());
        return mapper.toDto(cvRepository.save(cv));
    }

    @Transactional(readOnly = true)
    public CvDto consulter(Long guideId) {
        GuideEntity guide = guideService.charger(guideId);
        policy.verifierLecture(guide);
        CvEntity cv = cvRepository.findByGuideId(guideId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun CV pour ce guide."));
        return mapper.toDto(cv);
    }

    /** PDF du CV ; 409 si le CV n'existe pas. */
    @Transactional(readOnly = true)
    public byte[] exporter(Long guideId) {
        GuideEntity guide = guideService.charger(guideId);
        policy.verifierLecture(guide);
        CvEntity cv = cvRepository.findByGuideId(guideId)
                .orElseThrow(() -> new ConflictException("Le CV n'existe pas : export impossible."));
        return pdf.generer(guide, cv);
    }
}
