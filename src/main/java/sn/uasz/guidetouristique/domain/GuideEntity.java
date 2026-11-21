package sn.uasz.guidetouristique.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guides")
public class GuideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true)
    private String email;

    private String telephone;
    private String photoUrl;

    @Column(nullable = false)
    private String region;

    private String ville;
    private String adresseProfessionnelle;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "guide_specialites", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "specialite")
    @OrderColumn(name = "ordre")
    private List<String> specialites = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutGuide statut = StatutGuide.BROUILLON;

    /** Subject du JWT du createur du profil : base du controle de propriete. */
    @Column(nullable = false)
    private String proprietaire;

    @OneToOne(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private CvEntity cv;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }
    public String getAdresseProfessionnelle() { return adresseProfessionnelle; }
    public void setAdresseProfessionnelle(String a) { this.adresseProfessionnelle = a; }
    public List<String> getSpecialites() { return specialites; }
    public void setSpecialites(List<String> specialites) { this.specialites = specialites; }
    public StatutGuide getStatut() { return statut; }
    public void setStatut(StatutGuide statut) { this.statut = statut; }
    public String getProprietaire() { return proprietaire; }
    public void setProprietaire(String proprietaire) { this.proprietaire = proprietaire; }
    public CvEntity getCv() { return cv; }
    public void setCv(CvEntity cv) { this.cv = cv; }
}
