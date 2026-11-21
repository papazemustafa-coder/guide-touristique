package sn.uasz.guidetouristique.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.guidetouristique.domain.CvEntity;

public interface CvRepository extends JpaRepository<CvEntity, Long> {
    Optional<CvEntity> findByGuideId(Long guideId);
}
