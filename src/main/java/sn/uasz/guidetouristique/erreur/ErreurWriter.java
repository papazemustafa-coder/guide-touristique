package sn.uasz.guidetouristique.erreur;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/** Ecrit le schema Erreur hors MVC (filtres de securite, avant le @RestControllerAdvice). */
@Component
public class ErreurWriter {

    private final ObjectMapper mapper;

    public ErreurWriter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void ecrire(HttpServletResponse response, int status, ErrorCode code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        mapper.writeValue(response.getWriter(), new ErrorResponse(code, message, null));
    }
}
