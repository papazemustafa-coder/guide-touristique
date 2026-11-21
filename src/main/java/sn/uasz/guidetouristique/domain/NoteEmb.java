package sn.uasz.guidetouristique.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class NoteEmb {
    @Column(nullable = false)
    private String critere;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal note;

    @Column(nullable = false, precision = 12, scale = 4)
    private BigDecimal noteMaximale;

    public String getCritere() { return critere; }
    public void setCritere(String critere) { this.critere = critere; }
    public BigDecimal getNote() { return note; }
    public void setNote(BigDecimal note) { this.note = note; }
    public BigDecimal getNoteMaximale() { return noteMaximale; }
    public void setNoteMaximale(BigDecimal noteMaximale) { this.noteMaximale = noteMaximale; }
}
