package sn.uasz.guidetouristique;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import sn.uasz.guidetouristique.domain.Role;
import sn.uasz.guidetouristique.domain.StatutGuide;

/** Partie 1 (guides et CV) et Partie 4 (erreurs, securite). */
@SpringBootTest
@AutoConfigureMockMvc
class GuideApiTest extends ApiTestBase {

    @Test
    void sansJetonRetourne401AuFormatErreur() throws Exception {
        mvc.perform(get("/api/v1/guides"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTORISE"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void jetonInvalideRetourne401() throws Exception {
        mvc.perform(get("/api/v1/guides").header("Authorization", "Bearer n-importe-quoi"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("NON_AUTORISE"));
    }

    @Test
    void creationRetourne201EtStatutBrouillon() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Ziguinchor");
        mvc.perform(get("/api/v1/guides/" + id).header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("BROUILLON"))
                .andExpect(jsonPath("$.specialites[0]").value("Patrimoine"));
    }

    @Test
    void creationInvalideRetourne400() throws Exception {
        mvc.perform(post("/api/v1/guides").header("Authorization", bearer(unique(), Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prenom\":\"\",\"email\":\"pas-un-email\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));
    }

    @Test
    void champInconnuRefuseCarAdditionalPropertiesFalse() throws Exception {
        String corps = guideJson("Thies").replace("\"region\"", "\"statut\":\"ADMIS\",\"region\"");
        mvc.perform(post("/api/v1/guides").header("Authorization", bearer(unique(), Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));
    }

    @Test
    void emailDejaUtiliseRetourne409() throws Exception {
        String corps = guideJson("Thies");
        for (int i = 0; i < 2; i++) {
            mvc.perform(post("/api/v1/guides").header("Authorization", bearer(unique(), Role.GUIDE))
                            .contentType(MediaType.APPLICATION_JSON).content(corps))
                    .andExpect(status().is(i == 0 ? 201 : 409));
        }
    }

    @Test
    void guideInconnuRetourne404() throws Exception {
        mvc.perform(get("/api/v1/guides/999999").header("Authorization", bearer(unique(), Role.ADMIN)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("RESSOURCE_INTROUVABLE"));
    }

    @Test
    void unGuideNeVoitPasLeProfilDunAutre() throws Exception {
        long id = creerGuide(unique(), "Kolda");
        mvc.perform(get("/api/v1/guides/" + id).header("Authorization", bearer(unique(), Role.GUIDE)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCES_REFUSE"));
    }

    @Test
    void modificationDuProfil() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Kolda");
        String corps = guideJson("Kolda").replace("\"Awa\"", "\"Aminata\"");
        mvc.perform(put("/api/v1/guides/" + id).header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Aminata"));
    }

    @Test
    void rechercheInterditeAuxGuidesPaginee() throws Exception {
        String region = "Region-" + UUID.randomUUID();
        creerGuide(unique(), region);
        creerGuide(unique(), region);

        mvc.perform(get("/api/v1/guides").header("Authorization", bearer(unique(), Role.GUIDE)))
                .andExpect(status().isForbidden());

        mvc.perform(get("/api/v1/guides").param("region", region).param("taille", "1")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(header().string("X-Total-Count", "2"));

        mvc.perform(get("/api/v1/guides").param("region", region).param("taille", "1").param("page", "1")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE)))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void rechercheAvecTailleOuStatutInvalideRetourne400() throws Exception {
        mvc.perform(get("/api/v1/guides").param("taille", "0")
                        .header("Authorization", bearer(unique(), Role.ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));
        mvc.perform(get("/api/v1/guides").param("statut", "INCONNU")
                        .header("Authorization", bearer(unique(), Role.ADMIN)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rechercheParLangueEtStatut() throws Exception {
        String user = unique();
        String region = "Region-" + UUID.randomUUID();
        long id = creerGuide(user, region);
        enregistrerCv(user, id, CV_OK);
        creerGuide(unique(), region); // meme region, sans CV

        mvc.perform(get("/api/v1/guides").param("region", region).param("langue", "francais")
                        .param("statut", "BROUILLON")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(id));
    }

    @Test
    void laVersionDuCvEstIncrementeeAChaqueEnregistrement() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Thies");
        for (int attendu = 1; attendu <= 3; attendu++) {
            mvc.perform(put("/api/v1/guides/" + id + "/cv").header("Authorization", bearer(user, Role.GUIDE))
                            .contentType(MediaType.APPLICATION_JSON).content(CV_OK))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.version").value(attendu));
        }
        mvc.perform(get("/api/v1/guides/" + id + "/cv").header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(jsonPath("$.version").value(3))
                .andExpect(jsonPath("$.langues[0].langue").value("Francais"));
    }

    @Test
    void cvSansLangueOuSansFormationsRetourne400() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Thies");
        String sansLangue = CV_OK.replace("[{\"langue\":\"Francais\",\"niveau\":\"MATERNEL\"}]", "[]");
        mvc.perform(put("/api/v1/guides/" + id + "/cv").header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(sansLangue))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("REQUETE_INVALIDE"));

        mvc.perform(put("/api/v1/guides/" + id + "/cv").header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"experiences\":[],\"langues\":[{\"langue\":\"Wolof\",\"niveau\":\"MATERNEL\"}]}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cvRefuseSiDossierSoumis() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Thies");
        support.definirStatutGuide(id, StatutGuide.DOSSIER_SOUMIS);
        mvc.perform(put("/api/v1/guides/" + id + "/cv").header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(CV_OK))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLIT"));
    }

    @Test
    void suppressionRefuseeSiSoumisAutoriseeSinon() throws Exception {
        String user = unique();
        long soumis = creerGuide(user, "Thies");
        support.definirStatutGuide(soumis, StatutGuide.DOSSIER_SOUMIS);
        mvc.perform(delete("/api/v1/guides/" + soumis).header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(status().isConflict());

        long brouillon = creerGuide(user, "Thies");
        mvc.perform(delete("/api/v1/guides/" + brouillon).header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/guides/" + brouillon).header("Authorization", bearer(user, Role.ADMIN)))
                .andExpect(status().isNotFound());
    }

    @Test
    void exportSansCvRetourne409() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Thies");
        mvc.perform(post("/api/v1/guides/" + id + "/cv/export").header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CONFLIT"));
    }

    @Test
    void exportAvecCvRetourneUnPdf() throws Exception {
        String user = unique();
        long id = creerGuide(user, "Thies");
        enregistrerCv(user, id, CV_OK);
        MvcResult r = mvc.perform(post("/api/v1/guides/" + id + "/cv/export")
                        .header("Authorization", bearer(user, Role.GUIDE)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andReturn();
        String debut = new String(r.getResponse().getContentAsByteArray(), 0, 5, StandardCharsets.US_ASCII);
        assertThat(debut).isEqualTo("%PDF-");
    }
}
