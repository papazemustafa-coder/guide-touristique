package sn.uasz.guidetouristique;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import sn.uasz.guidetouristique.domain.Role;

/** Prouve que le seuil d'admission vient de application.yml et n'est pas code en dur. */
@SpringBootTest(properties = "app.resultat.seuil-admission=15")
@AutoConfigureMockMvc
class SeuilConfigurableTest extends ApiTestBase {

    @Test
    void avecUnSeuilDe15UneNoteDe14EstRefusee() throws Exception {
        Dossier d = dossierConvoque();
        mvc.perform(post("/api/v1/candidatures/" + d.candidatureId() + "/resultats")
                        .header("Authorization", bearer(unique(), Role.EVALUATEUR))
                        .contentType(MediaType.APPLICATION_JSON).content(resultatJson(NOTES_14, "ADMIS")))
                .andExpect(status().isBadRequest()); // 14.00 < 15 (alors qu'elle passe avec le seuil par defaut de 10)
    }
}
