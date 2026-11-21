package sn.uasz.guidetouristique.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import org.springframework.stereotype.Component;
import sn.uasz.guidetouristique.domain.CertificationEmb;
import sn.uasz.guidetouristique.domain.CvEntity;
import sn.uasz.guidetouristique.domain.ExperienceEmb;
import sn.uasz.guidetouristique.domain.FormationEmb;
import sn.uasz.guidetouristique.domain.GuideEntity;
import sn.uasz.guidetouristique.domain.LangueEmb;

/** Generation du PDF du CV avec OpenPDF (bibliotheque libre). */
@Component
public class CvPdfGenerator {

    public byte[] generer(GuideEntity guide, CvEntity cv) {
        try {
            ByteArrayOutputStream sortie = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            PdfWriter.getInstance(document, sortie);
            document.open();

            Font titre = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Font section = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13);
            Font texte = FontFactory.getFont(FontFactory.HELVETICA, 11);

            document.add(new Paragraph(guide.getPrenom() + " " + guide.getNom(), titre));
            if (cv.getTitreProfessionnel() != null) {
                document.add(new Paragraph(cv.getTitreProfessionnel(), texte));
            }
            document.add(new Paragraph(guide.getEmail() + " - " + guide.getRegion(), texte));
            document.add(new Paragraph("CV version " + cv.getVersion(), texte));

            if (cv.getProfilProfessionnel() != null && !cv.getProfilProfessionnel().isBlank()) {
                document.add(new Paragraph("\nProfil", section));
                document.add(new Paragraph(cv.getProfilProfessionnel(), texte));
            }

            document.add(new Paragraph("\nLangues", section));
            for (LangueEmb l : cv.getLangues()) {
                document.add(new Paragraph("- " + l.getLangue() + " (" + l.getNiveau() + ")", texte));
            }

            if (!cv.getFormations().isEmpty()) {
                document.add(new Paragraph("\nFormations", section));
                for (FormationEmb f : cv.getFormations()) {
                    String fin = f.getAnneeFin() == null ? "" : "-" + f.getAnneeFin();
                    document.add(new Paragraph("- " + f.getIntitule() + ", " + f.getEtablissement()
                            + " (" + f.getAnneeDebut() + fin + ")", texte));
                }
            }

            if (!cv.getExperiences().isEmpty()) {
                document.add(new Paragraph("\nExperiences", section));
                for (ExperienceEmb e : cv.getExperiences()) {
                    String periode = e.getDateDebut() + " -> "
                            + (e.isEnCours() || e.getDateFin() == null ? "en cours" : e.getDateFin());
                    document.add(new Paragraph("- " + e.getPoste() + ", " + e.getOrganisme()
                            + " (" + periode + ")", texte));
                }
            }

            if (!cv.getCertifications().isEmpty()) {
                document.add(new Paragraph("\nCertifications", section));
                for (CertificationEmb c : cv.getCertifications()) {
                    document.add(new Paragraph("- " + c.getIntitule() + ", " + c.getOrganisme(), texte));
                }
            }

            if (!cv.getCompetences().isEmpty()) {
                document.add(new Paragraph("\nCompetences", section));
                document.add(new Paragraph(String.join(", ", cv.getCompetences()), texte));
            }

            document.close();
            return sortie.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Echec de la generation du PDF", e);
        }
    }
}
