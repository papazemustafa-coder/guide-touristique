package sn.uasz.guidetouristique.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cvs")
public class CvEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id", unique = true, nullable = false)
    private GuideEntity guide;

    /** Version fonctionnelle : +1 a chaque enregistrement. */
    @Column(nullable = false)
    private int version;

    @Column(length = 200)
    private String titreProfessionnel;

    @Column(length = 3000)
    private String profilProfessionnel;

    private Instant dateMiseAJour;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cv_formations", joinColumns = @JoinColumn(name = "cv_id"))
    @OrderColumn(name = "ordre")
    private List<FormationEmb> formations = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cv_experiences", joinColumns = @JoinColumn(name = "cv_id"))
    @OrderColumn(name = "ordre")
    private List<ExperienceEmb> experiences = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cv_langues", joinColumns = @JoinColumn(name = "cv_id"))
    @OrderColumn(name = "ordre")
    private List<LangueEmb> langues = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cv_competences", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "competence")
    @OrderColumn(name = "ordre")
    private List<String> competences = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cv_certifications", joinColumns = @JoinColumn(name = "cv_id"))
    @OrderColumn(name = "ordre")
    private List<CertificationEmb> certifications = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "cv_references_pro", joinColumns = @JoinColumn(name = "cv_id"))
    @Column(name = "reference_pro")
    @OrderColumn(name = "ordre")
    private List<String> referencesProfessionnelles = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GuideEntity getGuide() { return guide; }
    public void setGuide(GuideEntity guide) { this.guide = guide; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getTitreProfessionnel() { return titreProfessionnel; }
    public void setTitreProfessionnel(String t) { this.titreProfessionnel = t; }
    public String getProfilProfessionnel() { return profilProfessionnel; }
    public void setProfilProfessionnel(String p) { this.profilProfessionnel = p; }
    public Instant getDateMiseAJour() { return dateMiseAJour; }
    public void setDateMiseAJour(Instant d) { this.dateMiseAJour = d; }
    public List<FormationEmb> getFormations() { return formations; }
    public void setFormations(List<FormationEmb> f) { this.formations = f; }
    public List<ExperienceEmb> getExperiences() { return experiences; }
    public void setExperiences(List<ExperienceEmb> e) { this.experiences = e; }
    public List<LangueEmb> getLangues() { return langues; }
    public void setLangues(List<LangueEmb> l) { this.langues = l; }
    public List<String> getCompetences() { return competences; }
    public void setCompetences(List<String> c) { this.competences = c; }
    public List<CertificationEmb> getCertifications() { return certifications; }
    public void setCertifications(List<CertificationEmb> c) { this.certifications = c; }
    public List<String> getReferencesProfessionnelles() { return referencesProfessionnelles; }
    public void setReferencesProfessionnelles(List<String> r) { this.referencesProfessionnelles = r; }
}
