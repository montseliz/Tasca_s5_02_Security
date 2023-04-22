package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.controller.mongodb;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.Message;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.AuthenticationResponseMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.LoginMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.RegisterMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.RegisterPlayerMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.EmailDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PasswordIncorrectException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mongodb.AuthenticationServiceMongo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/mongo")
@Tag(name = "Authentication MongoDB API", description = "API operations pertaining to Dice Game MongoDB security")
public class AuthenticationMongoController {

    private final AuthenticationServiceMongo authenticationServiceMongo;

    @Autowired
    public AuthenticationMongoController(AuthenticationServiceMongo authenticationServiceMongo) {
        this.authenticationServiceMongo = authenticationServiceMongo;
    }

    @PostMapping(value = "/register", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Register a player", description = "Registers a new player in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Player registered correctly", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = RegisterPlayerMongoDTO.class))}),
            @ApiResponse(responseCode = "409", description = "Player's email not valid", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "409", description = "Player's name not valid", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error while registering the player", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))})})

    public ResponseEntity<RegisterPlayerMongoDTO> register(@RequestBody RegisterMongoDTO registerMongoDTO) throws Exception {

        try {
            return new ResponseEntity<>(authenticationServiceMongo.createRegister(registerMongoDTO), HttpStatus.CREATED);
        } catch (EmailDuplicatedException | PlayerDuplicatedException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Internal Server Error while registering the player", e.getCause());
        }
    }

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    @Operation(summary = "Login a player", description = "Login a player in the application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player logged in correctly", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationResponseMongoDTO.class))}),
            @ApiResponse(responseCode = "404", description = "Player's email not found", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "401", description = "Player's password incorrect", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "404", description = "Player's token not valid", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error while logging in the player", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Message.class))})})

    public ResponseEntity<AuthenticationResponseMongoDTO> login(@RequestBody LoginMongoDTO loginMongoDTO) throws Exception {

        try {
            return new ResponseEntity<>(authenticationServiceMongo.authenticateRegister(loginMongoDTO), HttpStatus.OK);
        } catch (PlayerNotFoundException | PasswordIncorrectException | AuthenticationCredentialsNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new Exception("Internal Server Error while logging in the player", e.getCause());
        }
    }

}
