package sn.uasz.guidetouristique.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import sn.uasz.guidetouristique.domain.GuideEntity;

public interface GuideRepository extends JpaRepository<GuideEntity, Long>, JpaSpecificationExecutor<GuideEntity> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
