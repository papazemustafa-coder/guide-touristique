package sn.uasz.guidetouristique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class ExperienceEmb {
    @Column(nullable = false)
    private String poste;
    @Column(nullable = false)
    private String organisme;
    @Column(nullable = false)
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean enCours;
    @Column(length = 3000)
    private String description;
    private String region;

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }
    public String getOrganisme() { return organisme; }
    public void setOrganisme(String organisme) { this.organisme = organisme; }
    public LocalDate getDateDebut() { return dateDebut; }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut = dateDebut; }
    public LocalDate getDateFin() { return dateFin; }
    public void setDateFin(LocalDate dateFin) { this.dateFin = dateFin; }
    public boolean isEnCours() { return enCours; }
    public void setEnCours(boolean enCours) { this.enCours = enCours; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
}
