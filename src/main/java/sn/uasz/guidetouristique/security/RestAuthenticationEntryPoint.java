package sn.uasz.guidetouristique.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import sn.uasz.guidetouristique.erreur.ErreurWriter;
import sn.uasz.guidetouristique.erreur.ErrorCode;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ErreurWriter writer;

    public RestAuthenticationEntryPoint(ErreurWriter writer) {
        this.writer = writer;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {
        writer.ecrire(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.NON_AUTORISE,
                "Authentification requise ou jeton invalide.");
    }
}
