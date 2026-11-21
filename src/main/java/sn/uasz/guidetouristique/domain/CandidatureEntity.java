package sn.uasz.guidetouristique.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "candidatures",
        uniqueConstraints = @UniqueConstraint(columnNames = {"guide_id", "session_id"}))
public class CandidatureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guide_id")
    private GuideEntity guide;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id")
    private SessionEntity session;

    /** Version du CV au moment de la soumission. */
    @Column(nullable = false)
    private int versionCV;

    /** Copie FIGEE du CV (JSON) prise a la soumission : independante du CV vivant. */
    @Lob
    @Column(nullable = false)
    private String cvFige;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut = StatutCandidature.SOUMISE;

    @Column(nullable = false)
    private Instant dateSoumission;

    @Column(length = 2000)
    private String commentaire;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GuideEntity getGuide() { return guide; }
    public void setGuide(GuideEntity guide) { this.guide = guide; }
    public SessionEntity getSession() { return session; }
    public void setSession(SessionEntity session) { this.session = session; }
    public int getVersionCV() { return versionCV; }
    public void setVersionCV(int versionCV) { this.versionCV = versionCV; }
    public String getCvFige() { return cvFige; }
    public void setCvFige(String cvFige) { this.cvFige = cvFige; }
    public StatutCandidature getStatut() { return statut; }
    public void setStatut(StatutCandidature statut) { this.statut = statut; }
    public Instant getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(Instant dateSoumission) { this.dateSoumission = dateSoumission; }
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}
