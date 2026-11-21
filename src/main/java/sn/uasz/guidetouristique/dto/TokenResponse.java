package sn.uasz.guidetouristique.dto;

public record TokenResponse(String token, String type, long expiresInMinutes) {
}
