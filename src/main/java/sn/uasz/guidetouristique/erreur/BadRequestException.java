package sn.uasz.guidetouristique.erreur;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}
