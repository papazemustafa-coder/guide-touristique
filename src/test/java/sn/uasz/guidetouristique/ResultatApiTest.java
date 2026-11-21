package sn.uasz.guidetouristique;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import sn.uasz.guidetouristique.domain.Role;
import sn.uasz.guidetouristique.domain.StatutCandidature;
import sn.uasz.guidetouristique.domain.StatutGuide;

/** Partie 3 : saisie, calcul, coherence, statuts, publication et visibilite des resultats. */
@SpringBootTest
@AutoConfigureMockMvc
class ResultatApiTest extends ApiTestBase {

    private static final String NOTES_5 = "[{\"critere\":\"Patrimoine\",\"note\":5,\"noteMaximale\":20}]";

    private void enregistrer(long candidatureId, String notes, String decision, int statutAttendu) throws Exception {
        mvc.perform(post("/api/v1/candidatures/" + candidatureId + "/resultats")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR))
                        .contentType(MediaType.APPLICATION_JSON).content(resultatJson(notes, decision)))
                .andExpect(status().is(statutAttendu));
    }

    @Test
    void noteTotaleEstCalculeeSurVingtEtResultatCreeEnBrouillon() throws Exception {
        Dossier d = dossierConvoque();
        // (14 + 7) / (20 + 10) x 20 = 14.00
        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/resultats")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR))
                        .contentType(MediaType.APPLICATION_JSON).content(resultatJson(NOTES_14, "ADMIS")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.noteTotale").value(14.0))
                .andExpect(jsonPath("$.statutPublication").value("BROUILLON"))
                .andExpect(jsonPath("$.candidatureId").value(d.candidatureId()));
    }

    @Test
    void noteTotaleArrondieADeuxDecimales() throws Exception {
        Dossier d = dossierConvoque();
        // 10 / 30 x 20 = 6.666... -> 6.67 ; decision NON_ADMIS
        String notes = "[{\"critere\":\"A\",\"note\":5,\"noteMaximale\":10},{\"critere\":\"B\",\"note\":5,\"noteMaximale\":20}]";
        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/resultats")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR))
                        .contentType(MediaType.APPLICATION_JSON).content(resultatJson(notes, "NON_ADMIS")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.noteTotale").value(6.67));
    }

    @Test
    void admisSousLeSeuilRetourne400() throws Exception {
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), NOTES_5, "ADMIS", 400); // 5/20 -> 5.00 < 10
    }

    @Test
    void noteSuperieureANoteMaximaleRetourne400() throws Exception {
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), "[{\"critere\":\"A\",\"note\":25,\"noteMaximale\":20}]", "NON_ADMIS", 400);
    }

    @Test
    void corpsInvalideEtChampInconnuRetournent400() throws Exception {
        Dossier d = dossierConvoque();
        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/resultats")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evaluateurId\":1,\"notes\":[],\"decision\":\"ADMIS\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/resultats")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"evaluateurId\":1,\"notes\":" + NOTES_14 + ",\"decision\":\"ADMIS\",\"noteTotale\":20}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void decisionAdmisMetAJourCandidatureEtGuide() throws Exception {
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), NOTES_14, "ADMIS", 201);
        assertThat(support.lireStatutCandidature(d.candidatureId())).isEqualTo(StatutCandidature.ADMISE);
        assertThat(support.lireStatutGuide(d.guideId())).isEqualTo(StatutGuide.ADMIS);
    }

    @Test
    void decisionNonAdmisMetAJourCandidatureEtGuide() throws Exception {
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), NOTES_5, "NON_ADMIS", 201);
        assertThat(support.lireStatutCandidature(d.candidatureId())).isEqualTo(StatutCandidature.NON_ADMISE);
        assertThat(support.lireStatutGuide(d.guideId())).isEqualTo(StatutGuide.NON_ADMIS);
    }

    @Test
    void decisionAjourneLaisseLeGuideEnEvaluation() throws Exception {
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), NOTES_5, "AJOURNE", 201);
        assertThat(support.lireStatutCandidature(d.candidatureId())).isEqualTo(StatutCandidature.EVALUEE);
        assertThat(support.lireStatutGuide(d.guideId())).isEqualTo(StatutGuide.EN_EVALUATION);
    }

    @Test
    void resultatImpossibleSansConvocationOuEnDouble() throws Exception {
        // Candidature seulement SOUMISE
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long soumise = soumettre(user, guideId, creerSession(5));
        enregistrer(soumise, NOTES_14, "ADMIS", 409);

        // Deuxieme resultat sur une candidature deja evaluee
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), NOTES_14, "ADMIS", 201);
        enregistrer(d.candidatureId(), NOTES_14, "ADMIS", 409);
    }

    @Test
    void saisieReserveeAUnEvaluateurEtCandidatureInconnue404() throws Exception {
        Dossier d = dossierConvoque();
        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/resultats")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE))
                        .contentType(MediaType.APPLICATION_JSON).content(resultatJson(NOTES_14, "ADMIS")))
                .andExpect(status().isForbidden());
        enregistrer(999999, NOTES_14, "ADMIS", 404);
    }

    @Test
    void unCandidatNeVoitSonResultatQuApresPublication() throws Exception {
        Dossier d = dossierConvoque();
        enregistrer(d.candidatureId(), NOTES_14, "ADMIS", 201);
        String url = "/api/v1/candidatures/" + d.candidatureId() + "/resultats";

        // Brouillon : invisible pour le candidat, visible pour evaluateur et admin
        mvc.perform(get(url).header("Authorization", bearer(d.user(), Role.GUIDE)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESSOURCE_INTROUVABLE"));
        mvc.perform(get(url).header("Authorization", bearer(unique(), Role.EVALUATEUR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutPublication").value("BROUILLON"));
        mvc.perform(get(url).header("Authorization", bearer(unique(), Role.ADMIN)))
                .andExpect(status().isOk());

        // Publication : reservee evaluateur / admin
        mvc.perform(post(url + "/publication").header("Authorization", bearer(d.user(), Role.GUIDE)))
                .andExpect(status().isForbidden());
        mvc.perform(post(url + "/publication").header("Authorization", bearer(unique(), Role.EVALUATEUR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statutPublication").value("PUBLIE"));
        mvc.perform(post(url + "/publication").header("Authorization", bearer(unique(), Role.EVALUATEUR)))
                .andExpect(status().isConflict());

        // Publie : visible par son proprietaire, jamais par un autre guide
        mvc.perform(get(url).header("Authorization", bearer(d.user(), Role.GUIDE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision").value("ADMIS"));
        mvc.perform(get(url).header("Authorization", bearer(unique(), Role.GUIDE)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
        mvc.perform(get(url).header("Authorization", bearer(unique(), Role.GESTIONNAIRE)))
                .andExpect(status().isForbidden());
    }
}
