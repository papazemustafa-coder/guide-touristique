package sn.uasz.guidetouristique.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import sn.uasz.guidetouristique.erreur.ErreurWriter;
import sn.uasz.guidetouristique.erreur.ErrorCode;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ErreurWriter writer;

    public RestAccessDeniedHandler(ErreurWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException {
        writer.ecrire(response, HttpServletResponse.SC_FORBIDDEN, ErrorCode.ACCES_REFUSE, "Accès refusé.");
    }
}
