package sn.uasz.guidetouristique.erreur;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/** Produit exclusivement le schema Erreur du contrat (code, message, details). */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> introuvable(ResourceNotFoundException e) {
        return reponse(HttpStatus.NOT_FOUND, ErrorCode.RESSOURCE_INTROUVABLE, e.getMessage(), null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> routeInconnue(NoResourceFoundException e) {
        return reponse(HttpStatus.NOT_FOUND, ErrorCode.RESSOURCE_INTROUVABLE, "La ressource demandée n'existe pas.", null);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> conflit(ConflictException e) {
        return reponse(HttpStatus.CONFLICT, ErrorCode.CONFLIT, e.getMessage(), null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> requeteInvalide(BadRequestException e) {
        return reponse(HttpStatus.BAD_REQUEST, ErrorCode.REQUETE_INVALIDE, e.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validationCorps(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage()).toList();
        String message = details.isEmpty() ? "Données invalides." : details.get(0);
        return reponse(HttpStatus.BAD_REQUEST, ErrorCode.REQUETE_INVALIDE, message, String.join("; ", details));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> validationParametres(HandlerMethodValidationException e) {
        List<String> details = e.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage).toList();
        return reponse(HttpStatus.BAD_REQUEST, ErrorCode.REQUETE_INVALIDE, "Paramètres invalides.",
                String.join("; ", details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> contraintes(ConstraintViolationException e) {
        List<String> details = e.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage()).toList();
        return reponse(HttpStatus.BAD_REQUEST, ErrorCode.REQUETE_INVALIDE, "Paramètres invalides.",
                String.join("; ", details));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> illisible(Exception e) {
        return reponse(HttpStatus.BAD_REQUEST, ErrorCode.REQUETE_INVALIDE,
                "Requête illisible, champ inconnu ou paramètre invalide.", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> accesRefuse(AccessDeniedException e) {
        return reponse(HttpStatus.FORBIDDEN, ErrorCode.ACCES_REFUSE, "Accès refusé.", null);
    }

    private ResponseEntity<ErrorResponse> reponse(HttpStatus status, ErrorCode code, String message, String details) {
        return ResponseEntity.status(status).body(new ErrorResponse(code, message, details));
    }
}
