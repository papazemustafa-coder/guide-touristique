package sn.uasz.guidetouristique;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import sn.uasz.guidetouristique.domain.Role;
import sn.uasz.guidetouristique.domain.StatutCandidature;
import sn.uasz.guidetouristique.domain.StatutGuide;
import sn.uasz.guidetouristique.domain.StatutSession;

/** Partie 2 (candidatures) et Partie 3 (sessions, convocations). */
@SpringBootTest
@AutoConfigureMockMvc
class CandidatureApiTest extends ApiTestBase {

    // ---------- sessions ----------

    @Test
    void seulUnAdminCreeUneSession() throws Exception {
        mvc.perform(post("/api/v1/sessions-evaluation").header("Authorization", bearer(unique(), Role.GESTIONNAIRE))
                        .contentType(MediaType.APPLICATION_JSON).content(sessionJson(10)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));

        mvc.perform(post("/api/v1/sessions-evaluation").header("Authorization", bearer(unique(), Role.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content(sessionJson(10)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("OUVERTE"));
    }

    @Test
    void sessionAvecDatesIncoherentesOuCapaciteNulleRetourne400() throws Exception {
        String datesInversees = sessionJson(10)
                .replace("2027-01-10T09:00:00Z", "2027-02-01T09:00:00Z");
        mvc.perform(post("/api/v1/sessions-evaluation").header("Authorization", bearer(unique(), Role.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content(datesInversees))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));

        mvc.perform(post("/api/v1/sessions-evaluation").header("Authorization", bearer(unique(), Role.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content(sessionJson(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listeDesSessionsFiltreeParStatut() throws Exception {
        long id = creerSession(3);
        support.definirStatutSession(id, StatutSession.CLOTUREE);
        mvc.perform(get("/api/v1/sessions-evaluation").param("statut", "CLOTUREE")
                        .header("Authorization", bearer(unique(), Role.GUIDE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id==" + id + ")]").exists());
    }

    // ---------- soumission ----------

    @Test
    void soumissionFigeLeCvEtFaitEvoluerLeStatutDuGuide() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long sessionId = creerSession(5);

        mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(sessionId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("SOUMISE"))
                .andExpect(jsonPath("$.versionCV").value(1))
                .andExpect(jsonPath("$.guideId").value(guideId));

        assertThat(support.lireStatutGuide(guideId)).isEqualTo(StatutGuide.DOSSIER_SOUMIS);
    }

    @Test
    void uneModificationUlterieureDuCvNAltereJamaisLeCvRattacheALaCandidature() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long candidatureId = soumettre(user, guideId, creerSession(5));

        // On rend le CV a nouveau modifiable (hors parcours normal) puis on le modifie : version 2.
        support.definirStatutGuide(guideId, StatutGuide.BROUILLON);
        mvc.perform(put("/api/v1/guides/" + guideId + "/cv").header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CV_OK.replace("Guide patrimoine", "Titre modifie apres soumission")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(2));

        // Le CV vivant a change...
        mvc.perform(get("/api/v1/guides/" + guideId + "/cv").header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(jsonPath("$.titreProfessionnel").value("Titre modifie apres soumission"));

        // ... mais la copie figee de la candidature est intacte (version 1, ancien titre).
        JsonNode fige = json.readTree(support.lireCvFige(candidatureId));
        assertThat(fige.get("version").asInt()).isEqualTo(1);
        assertThat(fige.get("titreProfessionnel").asText()).isEqualTo("Guide patrimoine");
        mvc.perform(get("/api/v1/candidatures/" + candidatureId).header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(jsonPath("$.versionCV").value(1));
    }

    @Test
    void confirmationExactitudeDoitEtreTrue() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long sessionId = creerSession(5);
        for (String corps : new String[]{
                "{\"sessionId\":" + sessionId + ",\"confirmationExactitude\":false}",
                "{\"sessionId\":" + sessionId + "}"}) {
            mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                            .header("Authorization", bearer(user, Role.GUIDE))
                            .contentType(MediaType.APPLICATION_JSON).content(corps))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));
        }
    }

    @Test
    void commentaireTropLongRetourne400() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        String trop = "x".repeat(2001);
        mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + creerSession(5) + ",\"confirmationExactitude\":true,\"commentaire\":\"" + trop + "\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void dossierIncompletSansCvRetourne400() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(creerSession(5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));
    }

    @Test
    void sessionInconnueRetourne404() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(999999)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESSOURCE_INTROUVABLE"));
    }

    @Test
    void sessionNonOuverteRetourne409() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long sessionId = creerSession(5);
        support.definirStatutSession(sessionId, StatutSession.CLOTUREE);
        mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(sessionId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLIT"));
    }

    @Test
    void sessionCompleteRetourne409() throws Exception {
        long sessionId = creerSession(1);
        String u1 = unique();
        long g1 = creerGuide(u1, "Dakar");
        enregistrerCv(u1, g1, CV_OK);
        soumettre(u1, g1, sessionId);

        String u2 = unique();
        long g2 = creerGuide(u2, "Dakar");
        enregistrerCv(u2, g2, CV_OK);
        mvc.perform(post("/api/v1/guides/" + g2 + "/candidatures")
                        .header("Authorization", bearer(u2, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(sessionId)))
                .andExpect(status().isConflict());
    }

    @Test
    void uneSeuleCandidatureActiveParGuideEtParSession() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long sessionId = creerSession(5);
        soumettre(user, guideId, sessionId);
        mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(sessionId)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLIT"));
    }

    @Test
    void listeEtConsultationRespectentLaPropriete() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long candidatureId = soumettre(user, guideId, creerSession(5));

        mvc.perform(get("/api/v1/guides/" + guideId + "/candidatures").header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/v1/candidatures/" + candidatureId).header("Authorization", bearer(unique(), Role.GUIDE)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        mvc.perform(get("/api/v1/candidatures/" + candidatureId).header("Authorization", bearer(unique(), Role.EVALUATEUR)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/v1/candidatures/999999").header("Authorization", bearer(unique(), Role.ADMIN)))
                .andExpect(status().isNotFound());
    }

    // ---------- convocation ----------

    @Test
    void convocationFaitPasserLaCandidatureAConvoqueeEtLeGuideAEnEvaluation() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long candidatureId = soumettre(user, guideId, creerSession(5));

        mvc.perform(post("/api/v1/candidatures/" + candidatureId + "/convocation")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateHeure\":\"2027-01-10T09:00:00Z\",\"lieu\":\"UCAD\",\"instructions\":\"CNI\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("ENVOYEE"))
                .andExpect(jsonPath("$.candidatureId").value(candidatureId));

        assertThat(support.lireStatutCandidature(candidatureId)).isEqualTo(StatutCandidature.CONVOQUEE);
        assertThat(support.lireStatutGuide(guideId)).isEqualTo(StatutGuide.EN_EVALUATION);
    }

    @Test
    void convocationSeulementSiSoumiseEtReserveeAuGestionnaire() throws Exception {
        Dossier d = dossierConvoque();
        String corps = "{\"dateHeure\":\"2027-01-10T09:00:00Z\",\"lieu\":\"UCAD\"}";

        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/convocation")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE))
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLIT"));

        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/convocation")
                        .header("Authorization", bearer(d.user(), Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isForbidden());

        mvc.perform(post("/api/v1/candidatures/999999/convocation")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE))
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isNotFound());
    }
}
