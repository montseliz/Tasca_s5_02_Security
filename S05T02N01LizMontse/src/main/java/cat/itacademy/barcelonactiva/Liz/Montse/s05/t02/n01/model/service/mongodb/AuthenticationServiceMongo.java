package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mongodb;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.PlayerMongo;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.RoleMongo;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.AuthenticationResponseMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.LoginMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.RegisterMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.RegisterPlayerMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.EmailDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PasswordIncorrectException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mongodb.IPlayerMongoRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.security.JwtGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceMongo {

    @Autowired
    private ModelMapper modelMapper;

    private final AuthenticationManager authenticationManager;
    private final IPlayerMongoRepository playerMongoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public AuthenticationServiceMongo(AuthenticationManager authenticationManager, IPlayerMongoRepository playerMongoRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.playerMongoRepository = playerMongoRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    /**
     * Mètode per crear un registre a la base de dades. S'utilitza en el mètode register() de l'AuthenticationController.
     */
    public RegisterPlayerMongoDTO createRegister(RegisterMongoDTO registerDTO) {

        RegisterMongoDTO registerEmailValidated = validateRegisterEmail(registerDTO);
        RegisterMongoDTO registerEmailAndNameValidated = validateRegisterName(registerEmailValidated);

        PlayerMongo player = new PlayerMongo(registerEmailAndNameValidated.getName(), registerEmailAndNameValidated.getEmail(), passwordEncoder.encode(registerEmailAndNameValidated.getPassword()));

        assignRoleToPlayer(player);
        playerMongoRepository.save(player);

        return modelMapper.map(player, RegisterPlayerMongoDTO.class);
    }

    /**
     * Mètode per comprovar si l'email del Player a registrar és únic perquè no es repeteixi a la base de dades.
     * S'utilitza en el mètode createRegister().
     */
    public RegisterMongoDTO validateRegisterEmail (RegisterMongoDTO registerDTO) {
        if (playerMongoRepository.existsByEmail(registerDTO.getEmail())) {
            throw new EmailDuplicatedException("Email introduced already exists");
        } else {
            return registerDTO;
        }
    }

    /**
     * Mètode per comprovar si el nom del Player a registrar és únic perquè no es repeteixi a la base de dades i mentre no sigui "unknown".
     * S'utilitza en el mètode createRegister().
     */
    public RegisterMongoDTO validateRegisterName (RegisterMongoDTO registerDTO) {
        RegisterMongoDTO playerUnknown = createUnknownName(registerDTO);
        if ((playerMongoRepository.existsByName(playerUnknown.getName())) && (!registerDTO.getName().equalsIgnoreCase("unknown"))) {
            throw new PlayerDuplicatedException("Player's name must be unique");
        } else {
            return registerDTO;
        }
    }

    /**
     * Mètode per generar el nom del Player a registrar a "unknown" en cas que sigui null, buit o en blanc.
     * S'utilitza en el mètode validateRegisterName().
     */
    public RegisterMongoDTO createUnknownName (RegisterMongoDTO registerDTO) {
        if ((registerDTO.getName() == null) || (registerDTO.getName().isEmpty()) || (registerDTO.getName().isBlank())) {
            registerDTO.setName("unknown");
        }
        return registerDTO;
    }

    /**
     * Mètode per assignar el role "USER" al player. S'utilitza en el mètode createRegister().
     */
    public void assignRoleToPlayer(PlayerMongo player) {
        RoleMongo role;
        Optional<PlayerMongo> playerRole = playerMongoRepository.findByRole("USER");

        if (playerRole.isPresent()) {
            role = playerRole.get().getRole();
        } else {
            role = new RoleMongo("USER");
        }

        player.setRole(role);
    }

    /**
     * Mètode per autenticar el player. S'utilitza en el mètode login() de l'AuthenticationController.
     */
    public AuthenticationResponseMongoDTO authenticateRegister(LoginMongoDTO loginDTO) {
        LoginMongoDTO loginDTOValidated = validateLoginEmailAndPassword(loginDTO);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTOValidated.getEmail(), loginDTOValidated.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        if (!jwtGenerator.isTokenValid(token)) {
            throw new AuthenticationCredentialsNotFoundException("Token generated is not valid");
        }

        return new AuthenticationResponseMongoDTO(loginDTOValidated.getEmail(), passwordEncoder.encode(loginDTOValidated.getPassword()), token);
    }

    /**
     * Mètode per validar l'email i el password a la base de dades. S`utilitza en el mètode authenticateRegister().
     */
    public LoginMongoDTO validateLoginEmailAndPassword (LoginMongoDTO loginDTO) {
        RegisterPlayerMongoDTO registerPlayerDTO = findEmailInDB(loginDTO);
        String encodedPassword = registerPlayerDTO.getPassword();
        String password = loginDTO.getPassword();

        if (passwordEncoder.matches(password, encodedPassword)) {
            return loginDTO;
        } else {
            throw new PasswordIncorrectException("Password incorrect, please try again");
        }
    }

    /**
     * Mètode per trobar el player a través de l'email, a la base de dades. S'utilitza en el mètode validateLoginEmailAndPassword().
     */
    public RegisterPlayerMongoDTO findEmailInDB(LoginMongoDTO loginDTO) {

        PlayerMongo player;
        Optional<PlayerMongo> emailFromDB = playerMongoRepository.findByEmail(loginDTO.getEmail());

        if (emailFromDB.isPresent()) {
            player = emailFromDB.get();
        } else {
            throw new PlayerNotFoundException("Player's email not found or incorrect");
        }

        return modelMapper.map(player, RegisterPlayerMongoDTO.class);
    }

}
