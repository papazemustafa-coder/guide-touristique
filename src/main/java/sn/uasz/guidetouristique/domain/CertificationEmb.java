package sn.uasz.guidetouristique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
public class CertificationEmb {
    @Column(nullable = false)
    private String intitule;
    @Column(nullable = false)
    private String organisme;
    private LocalDate dateObtention;
    @Column(name = "reference_cert")
    private String reference;

    public String getIntitule() { return intitule; }
    public void setIntitule(String intitule) { this.intitule = intitule; }
    public String getOrganisme() { return organisme; }
    public void setOrganisme(String organisme) { this.organisme = organisme; }
    public LocalDate getDateObtention() { return dateObtention; }
    public void setDateObtention(LocalDate dateObtention) { this.dateObtention = dateObtention; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
