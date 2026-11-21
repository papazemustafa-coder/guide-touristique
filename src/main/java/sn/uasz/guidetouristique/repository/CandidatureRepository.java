package sn.uasz.guidetouristique.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.guidetouristique.domain.CandidatureEntity;

public interface CandidatureRepository extends JpaRepository<CandidatureEntity, Long> {
    List<CandidatureEntity> findByGuideIdOrderByIdAsc(Long guideId);
    boolean existsByGuideIdAndSessionId(Long guideId, Long sessionId);
    long countBySessionId(Long sessionId);
}
