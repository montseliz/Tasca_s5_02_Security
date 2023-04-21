package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(EmailDuplicatedException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<Message> emailDuplicatedExceptionHandler (EmailDuplicatedException exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.BAD_REQUEST.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordIncorrectException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Message> passwordIncorrectExceptionHandler (PasswordIncorrectException exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.UNAUTHORIZED.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(PlayerNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Message> playerNotFoundExceptionHandler(PlayerNotFoundException exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GamesNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Message> gamesNotFoundExceptionHandler(GamesNotFoundException exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ResponseEntity<Message> authenticationCredentialsNotFoundExceptionHandler(AuthenticationCredentialsNotFoundException exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.NOT_FOUND.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlayerDuplicatedException.class)
    @ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
    public ResponseEntity<Message> playerDuplicatedExceptionHandler(PlayerDuplicatedException exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.NOT_ACCEPTABLE.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Message> internalServerErrorExceptionHandler(Exception exception, WebRequest request) {

        return new ResponseEntity<>(new Message(HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now(), exception.getMessage(), request.getDescription(false)), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}