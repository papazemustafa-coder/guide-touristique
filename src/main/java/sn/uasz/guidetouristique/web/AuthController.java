package sn.uasz.guidetouristique.web;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sn.uasz.guidetouristique.dto.TokenRequest;
import sn.uasz.guidetouristique.dto.TokenResponse;
import sn.uasz.guidetouristique.security.JwtService;

/** Emission de jetons SIMULES pour le TP : a ne jamais exposer tel quel en production. */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public TokenResponse token(@Valid @RequestBody TokenRequest req) {
        return new TokenResponse(jwtService.generer(req.username(), req.role()),
                "Bearer", jwtService.expirationMinutes());
    }
}
