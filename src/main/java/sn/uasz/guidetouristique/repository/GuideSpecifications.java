package sn.uasz.guidetouristique.repository;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import sn.uasz.guidetouristique.domain.CvEntity;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.LangueEmb;
import sn.uasz.guidetouristique.domain.StatutGuide;

public final class GuideSpecifications {

    private GuideSpecifications() {
    }

    public static Specification<GuideEntity> recherche(String nom, String langue, String region, StatutGuide statut) {
        return (root, query, cb) -> {
            List<Predicate> predicats = new ArrayList<>();

            if (StringUtils.hasText(nom)) {
                String motif = "%" + nom.trim().toLowerCase() + "%";
                predicats.add(cb.or(
                        cb.like(cb.lower(root.get("nom")), motif),
                        cb.like(cb.lower(root.get("prenom")), motif)));
            }
            if (StringUtils.hasText(region)) {
                predicats.add(cb.equal(cb.lower(root.get("region")), region.trim().toLowerCase()));
            }
            if (statut != null) {
                predicats.add(cb.equal(root.get("statut"), statut));
            }
            if (StringUtils.hasText(langue)) {
                Subquery<Long> sq = query.subquery(Long.class);
                Root<CvEntity> cv = sq.from(CvEntity.class);
                Join<CvEntity, LangueEmb> l = cv.join("langues");
                sq.select(cv.get("id")).where(
                        cb.equal(cv.get("guide"), root),
                        cb.equal(cb.lower(l.get("langue")), langue.trim().toLowerCase()));
                predicats.add(cb.exists(sq));
            }
            return cb.and(predicats.toArray(new Predicate[0]));
        };
    }
}
