package sn.uasz.guidetouristique;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import sn.uasz.guidetouristique.domain.Role;
import sn.uasz.guidetouristique.security.JwtService;

/** Outils communs aux tests d'API. */
abstract class ApiTestBase {

    @Autowired protected MockMvc mvc;
    @Autowired protected JwtService jwt;
    @Autowired protected ObjectMapper json;
    @Autowired protected TestSupport support;

    protected static final String CV_OK = """
            {"titreProfessionnel":"Guide patrimoine",
             "formations":[{"intitule":"Licence Tourisme","etablissement":"UASZ","anneeDebut":2011,"anneeFin":2014,"diplomeObtenu":true}],
             "experiences":[{"poste":"Guide","organisme":"Casamance Tours","dateDebut":"2015-01-01","enCours":true}],
             "langues":[{"langue":"Francais","niveau":"MATERNEL"}],
             "competences":["Histoire"]}""";

    protected static final String NOTES_14 = """
            [{"critere":"Patrimoine","note":14,"noteMaximale":20},{"critere":"Pratique","note":7,"noteMaximale":10}]""";

    protected String bearer(String user, Role role) {
        return "Bearer " + jwt.generer(user, role);
    }

    protected String unique() {
        return "user-" + UUID.randomUUID();
    }

    protected String guideJson(String region) {
        return """
                {"prenom":"Awa","nom":"Diop","email":"%s@example.sn","telephone":"+221770000000",
                 "region":"%s","specialites":["Patrimoine"]}""".formatted(UUID.randomUUID(), region);
    }

    protected long idDe(MvcResult r) throws Exception {
        return json.readTree(r.getResponse().getContentAsString()).get("id").asLong();
    }

    protected long creerGuide(String user, String region) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/guides")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(guideJson(region)))
                .andExpect(status().isCreated()).andReturn();
        return idDe(r);
    }

    protected void enregistrerCv(String user, long guideId, String corps) throws Exception {
        mvc.perform(put("/api/v1/guides/" + guideId + "/cv")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(corps))
                .andExpect(status().isOk());
    }

    protected String sessionJson(int capacite) {
        return """
                {"intitule":"Examen %s","region":"Dakar","lieu":"UCAD",
                 "dateDebut":"2027-01-10T09:00:00Z","dateFin":"2027-01-10T17:00:00Z",
                 "capacite":%d,"epreuves":["Oral"]}""".formatted(UUID.randomUUID(), capacite);
    }

    protected long creerSession(int capacite) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/sessions-evaluation")
                        .header("Authorization", bearer(unique(), Role.ADMIN))
                        .contentType(MediaType.APPLICATION_JSON).content(sessionJson(capacite)))
                .andExpect(status().isCreated()).andReturn();
        return idDe(r);
    }

    protected String candidatureJson(long sessionId) {
        return "{\"sessionId\":" + sessionId + ",\"confirmationExactitude\":true}";
    }

    protected long soumettre(String user, long guideId, long sessionId) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/guides/" + guideId + "/candidatures")
                        .header("Authorization", bearer(user, Role.GUIDE))
                        .contentType(MediaType.APPLICATION_JSON).content(candidatureJson(sessionId)))
                .andExpect(status().isCreated()).andReturn();
        return idDe(r);
    }

    protected void convoquer(long candidatureId) throws Exception {
        mvc.perform(post("/api/v1/candidatures/" + candidatureId + "/convocation")
                        .header("Authorization", bearer(unique(), Role.GESTIONNAIRE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dateHeure\":\"2027-01-10T09:00:00Z\",\"lieu\":\"UCAD\"}"))
                .andExpect(status().isOk());
    }

    protected String resultatJson(String notes, String decision) {
        return "{\"evaluateurId\":1,\"notes\":" + notes + ",\"decision\":\"" + decision + "\",\"appreciation\":\"RAS\"}";
    }

    /** Guide complet (profil + CV) dont la candidature est SOUMISE puis CONVOQUEE. */
    protected record Dossier(String user, long guideId, long candidatureId) {
    }

    protected Dossier dossierConvoque() throws Exception {
        String user = unique();
        long guideId = creerGuide(user, "Dakar");
        enregistrerCv(user, guideId, CV_OK);
        long candidatureId = soumettre(user, guideId, creerSession(5));
        convoquer(candidatureId);
        return new Dossier(user, guideId, candidatureId);
    }
}
