package sn.uasz.guidetouristique.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "resultats")
public class ResultatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidature_id", unique = true, nullable = false)
    private CandidatureEntity candidature;

    @Column(nullable = false)
    private Long evaluateurId;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "resultat_notes", joinColumns = @JoinColumn(name = "resultat_id"))
    @OrderColumn(name = "ordre")
    private List<NoteEmb> notes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    @Column(length = 3000)
    private String appreciation;

    @Column(nullable = false)
    private Instant dateEvaluation;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal noteTotale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPublication statutPublication = StatutPublication.BROUILLON;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public CandidatureEntity getCandidature() { return candidature; }
    public void setCandidature(CandidatureEntity candidature) { this.candidature = candidature; }
    public Long getEvaluateurId() { return evaluateurId; }
    public void setEvaluateurId(Long evaluateurId) { this.evaluateurId = evaluateurId; }
    public List<NoteEmb> getNotes() { return notes; }
    public void setNotes(List<NoteEmb> notes) { this.notes = notes; }
    public Decision getDecision() { return decision; }
    public void setDecision(Decision decision) { this.decision = decision; }
    public String getAppreciation() { return appreciation; }
    public void setAppreciation(String appreciation) { this.appreciation = appreciation; }
    public Instant getDateEvaluation() { return dateEvaluation; }
    public void setDateEvaluation(Instant dateEvaluation) { this.dateEvaluation = dateEvaluation; }
    public BigDecimal getNoteTotale() { return noteTotale; }
    public void setNoteTotale(BigDecimal noteTotale) { this.noteTotale = noteTotale; }
    public StatutPublication getStatutPublication() { return statutPublication; }
    public void setStatutPublication(StatutPublication s) { this.statutPublication = s; }
}
