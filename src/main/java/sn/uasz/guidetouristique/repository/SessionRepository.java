package sn.uasz.guidetouristique.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sn.uasz.guidetouristique.domain.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, Long>, JpaSpecificationExecutor<SessionEntity> {
    boolean existsByIntituleIgnoreCaseAndRegionIgnoreCaseAndDateDebut(String intitule, String region, Instant dateDebut);
}
