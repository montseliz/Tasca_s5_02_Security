package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.controller.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.AuthenticationResponseDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.LoginDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterPlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.EmailDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PasswordIncorrectException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/register")
    public ResponseEntity<RegisterPlayerDTO> register(@RequestBody RegisterDTO registerDTO) throws Exception {

        try {
            return new ResponseEntity<>(authenticationService.createRegister(registerDTO), HttpStatus.CREATED);
        } catch (EmailDuplicatedException | PlayerDuplicatedException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Internal Server Error while registering the player", e.getCause());
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginDTO loginDTO) throws Exception {

        try {
            return new ResponseEntity<>(authenticationService.authenticateRegister(loginDTO), HttpStatus.OK);
        } catch (PlayerNotFoundException | PasswordIncorrectException | AuthenticationCredentialsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Internal Server Error while logging in", e.getCause());
        }
    }

}
