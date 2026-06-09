package fr.jlndev.jrHotel.Integration;

import fr.jlndev.jrHotel.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TestSecurityExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<Response> handleAuthorizationDeniedException(
            AuthorizationDeniedException exception
    ) {
        Response response = new Response();
        response.setStatusCode(403);
        response.setMessage("Access Denied");

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
}