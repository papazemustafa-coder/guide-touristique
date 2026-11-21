package sn.uasz.guidetouristique.erreur;

/** Schema Erreur du contrat : code, message, details (chaine ou null). */
public record ErrorResponse(ErrorCode code, String message, String details) {
}
