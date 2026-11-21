package sn.uasz.guidetouristique.repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sn.uasz.guidetouristique.domain.SessionEntity;
import sn.uasz.guidetouristique.domain.StatutSession;

public final class SessionSpecifications {

    private SessionSpecifications() {
    }

    public static Specification<SessionEntity> recherche(String region, StatutSession statut) {
        return (root, query, cb) -> {
            List<Predicate> predicats = new ArrayList<>();
            if (StringUtils.hasText(region)) {
                predicats.add(cb.equal(cb.lower(root.get("region")), region.trim().toLowerCase()));
            }
            if (statut != null) {
                predicats.add(cb.equal(root.get("statut"), statut));
            }
            return cb.and(predicats.toArray(new Predicate[0]));
        };
    }
}
