package sn.uasz.guidetouristique.domain;

import jakarta.persistence.*;

@Embeddable
public class LangueEmb {
    @Column(nullable = false)
    private String langue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauLangue niveau;

    public String getLangue() { return langue; }
    public void setLangue(String langue) { this.langue = langue; }
    public NiveauLangue getNiveau() { return niveau; }
    public void setNiveau(NiveauLangue niveau) { this.niveau = niveau; }
}
