package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.AuthenticationResponseDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.LoginDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterPlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.EmailDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PasswordIncorrectException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerDuplicatedException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IPlayerRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IRoleRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.security.mysql.JwtGenerator;
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
public class AuthenticationService {

    @Autowired
    private ModelMapper modelMapper;

    private final AuthenticationManager authenticationManager;
    private final IPlayerRepository playerRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager, IPlayerRepository playerRepository, IRoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.playerRepository = playerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    /**
     * Mètode per crear un registre a la base de dades. S'utilitza en el mètode register() de l'AuthenticationController.
     */
    public RegisterPlayerDTO createRegister(RegisterDTO registerDTO) {

        RegisterDTO registerEmailValidated = validateRegisterEmail(registerDTO);
        RegisterDTO registerEmailAndNameValidated = validateRegisterName(registerEmailValidated);

        Player player = new Player (registerEmailAndNameValidated.getName(), registerEmailAndNameValidated.getEmail(), passwordEncoder.encode(registerEmailAndNameValidated.getPassword()));

        player.setRole(assignRoleToPlayer());
        playerRepository.save(player);

        return modelMapper.map(player, RegisterPlayerDTO.class);
    }

    /**
     * Mètode per comprovar si l'email del Player a registrar és únic perquè no es repeteixi a la base de dades.
     * S'utilitza en el mètode createRegister().
     */
    public RegisterDTO validateRegisterEmail (RegisterDTO registerDTO) {
        if (playerRepository.existsByEmail(registerDTO.getEmail())) {
            throw new EmailDuplicatedException("Email introduced already exists");
        } else {
            return registerDTO;
        }
    }

    /**
     * Mètode per comprovar si el nom del Player a registrar és únic perquè no es repeteixi a la base de dades i mentre no sigui "unknown".
     * S'utilitza en el mètode createRegister().
     */
    public RegisterDTO validateRegisterName (RegisterDTO registerDTO) {
        RegisterDTO playerUnknown =createUnknownName(registerDTO);
        if ((playerRepository.existsByName(playerUnknown.getName())) && (!registerDTO.getName().equalsIgnoreCase("unknown"))) {
            throw new PlayerDuplicatedException("Player's name must be unique");
        } else {
            return registerDTO;
        }
    }

    /**
     * Mètode per generar el nom del Player a registrar a "unknown" en cas que sigui null, buit o en blanc.
     * S'utilitza en el mètode validateRegisterName().
     */
    public RegisterDTO createUnknownName (RegisterDTO registerDTO) {
        if ((registerDTO.getName() == null) || (registerDTO.getName().isEmpty()) || (registerDTO.getName().isBlank())) {
            registerDTO.setName("unknown");
        }
        return registerDTO;
    }

    /**
     * Mètode per assignar el role "USER" al player. S'utilitza en el mètode createRegister().
     */
    public Role assignRoleToPlayer() {
        Role role;
        Optional<Role> playerRole = roleRepository.findByName("USER");
        if (playerRole.isPresent()) {
            role = playerRole.get();
        } else {
            role = new Role("USER");
            roleRepository.save(role);
        }
        return role;
    }

    /**
     * Mètode per autenticar el player. S'utilitza en el mètode login() de l'AuthenticationController.
     */
    public AuthenticationResponseDTO authenticateRegister(LoginDTO loginDTO) {
        LoginDTO loginDTOValidated = validateLoginEmailAndPassword(loginDTO);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTOValidated.getEmail(), loginDTOValidated.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        if (!jwtGenerator.isTokenValid(token)) {
            throw new AuthenticationCredentialsNotFoundException("Token generated is not valid");
        }

        return new AuthenticationResponseDTO(loginDTOValidated.getEmail(), passwordEncoder.encode(loginDTOValidated.getPassword()), token);
    }

    /**
     * Mètode per validar l'email i el password a la base de dades. S`utilitza en el mètode authenticateRegister().
     */
    public LoginDTO validateLoginEmailAndPassword (LoginDTO loginDTO) {
        RegisterPlayerDTO registerPlayerDTO = findEmailInDB(loginDTO);
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
    public RegisterPlayerDTO findEmailInDB(LoginDTO loginDTO) {

        Player player;
        Optional<Player> emailFromDB = playerRepository.findByEmail(loginDTO.getEmail());

        if (emailFromDB.isPresent()) {
            player = emailFromDB.get();
        } else {
            throw new PlayerNotFoundException("Player's email not found or incorrect");
        }

        return modelMapper.map(player, RegisterPlayerDTO.class);
    }

}
