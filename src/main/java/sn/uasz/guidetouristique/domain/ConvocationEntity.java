package sn.uasz.guidetouristique.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "convocations")
public class ConvocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidature_id", unique = true, nullable = false)
    private CandidatureEntity candidature;

    @Column(nullable = false)
    private Instant dateHeure;

    @Column(nullable = false)
    private String lieu;

    @Column(length = 2000)
    private String instructions;

    private Instant dateLimiteConfirmation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutConvocation statut = StatutConvocation.ENVOYEE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CandidatureEntity getCandidature() { return candidature; }
    public void setCandidature(CandidatureEntity candidature) { this.candidature = candidature; }
    public Instant getDateHeure() { return dateHeure; }
    public void setDateHeure(Instant dateHeure) { this.dateHeure = dateHeure; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public Instant getDateLimiteConfirmation() { return dateLimiteConfirmation; }
    public void setDateLimiteConfirmation(Instant d) { this.dateLimiteConfirmation = d; }
    public StatutConvocation getStatut() { return statut; }
    public void setStatut(StatutConvocation statut) { this.statut = statut; }
}
