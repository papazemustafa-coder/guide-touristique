package sn.uasz.guidetouristique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class FormationEmb {
    @Column(nullable = false)
    private String intitule;
    @Column(nullable = false)
    private String etablissement;
    @Column(nullable = false)
    private int anneeDebut;
    private Integer anneeFin;
    private String description;
    private boolean diplomeObtenu;

    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public String getEtablissement() { return etablissement; }
    public void setEtablissement(String etablissement) { this.etablissement = etablissement; }
    public int getAnneeDebut() { return anneeDebut; }
    public void setAnneeDebut(int anneeDebut) { this.anneeDebut = anneeDebut; }
    public Integer getAnneeFin() { return anneeFin; }
    public void setAnneeFin(Integer anneeFin) { this.anneeFin = anneeFin; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isDiplomeObtenu() { return diplomeObtenu; }
    public void setDiplomeObtenu(boolean diplomeObtenu) { this.diplomeObtenu = diplomeObtenu; }
}
