package sn.uasz.guidetouristique.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.uasz.guidetouristique.domain.ConvocationEntity;

public interface ConvocationRepository extends JpaRepository<ConvocationEntity, Long> {
    boolean existsByCandidatureId(Long candidatureId);
}
