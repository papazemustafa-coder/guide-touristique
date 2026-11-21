package sn.uasz.guidetouristique.security;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Parametres metier du resultat (application.yml : app.resultat.*). */
@ConfigurationProperties(prefix = "app.resultat")
public record ResultatProperties(BigDecimal seuilAdmission) {
}
