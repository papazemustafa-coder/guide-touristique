package sn.uasz.guidetouristique.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.Role;

/** Controle de propriete : un GUIDE n'accede qu'a ses propres ressources. */
@Component
public class GuideAccessPolicy {

    private final CurrentUser user;

    public GuideAccessPolicy(CurrentUser user) {
        this.user = user;
    }

    private boolean estProprietaire(GuideEntity g) {
        return user.hasRole(Role.GUIDE) && g.getProprietaire().equals(user.username());
    }

    /** Lecture : agents (ADMIN, GESTIONNAIRE, EVALUATEUR) ou proprietaire. */
    public void verifierLecture(GuideEntity g) {
        if (user.hasAnyRole(Role.ADMIN, Role.GESTIONNAIRE, Role.EVALUATEUR) || estProprietaire(g)) return;
        throw new AccessDeniedException("Accès refusé");
    }

    /** Ecriture sur le profil : ADMIN, GESTIONNAIRE ou proprietaire. */
    public void verifierEcriture(GuideEntity g) {
        if (user.hasAnyRole(Role.ADMIN, Role.GESTIONNAIRE) || estProprietaire(g)) return;
        throw new AccessDeniedException("Accès refusé");
    }

    /** Ecriture du CV et soumission d'une candidature : ADMIN ou proprietaire. */
    public void verifierEcritureCv(GuideEntity g) {
        if (user.hasRole(Role.ADMIN) || estProprietaire(g)) return;
        throw new AccessDeniedException("Accès refusé");
    }

    /** Resultats : acces complet reserve aux EVALUATEUR et ADMIN. */
    public boolean accesCompletResultat() {
        return user.hasAnyRole(Role.ADMIN, Role.EVALUATEUR);
    }

    /** Exige que l'appelant soit le candidat proprietaire du dossier. */
    public void verifierProprietaire(GuideEntity g) {
        if (estProprietaire(g)) return;
        throw new AccessDeniedException("Accès refusé");
    }
}
