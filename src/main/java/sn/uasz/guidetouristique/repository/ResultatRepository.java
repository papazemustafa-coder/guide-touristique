package sn.uasz.guidetouristique.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.guidetouristique.domain.ResultatEntity;

public interface ResultatRepository extends JpaRepository<ResultatEntity, Long> {
    Optional<ResultatEntity> findByCandidatureId(Long candidatureId);
}
