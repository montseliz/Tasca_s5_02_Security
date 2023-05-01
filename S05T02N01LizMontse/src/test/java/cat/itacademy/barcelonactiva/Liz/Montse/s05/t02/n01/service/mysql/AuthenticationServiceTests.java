package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.service.mysql;

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
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.AuthenticationService;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.security.JwtGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTests {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private IPlayerRepository playerRepository;
    @Mock
    private IRoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtGenerator jwtGenerator;
    @Spy
    ModelMapper modelMapper;

    @InjectMocks
    private AuthenticationService authenticationService;

    @DisplayName("Test to validate if the email and the name are unique in the database")
    @Test
    void testValidateRegisterEmailAndNameUnique() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        RegisterDTO registerDTO = RegisterDTO.builder()
                .name("Montse")
                .email("montse@gmail.com")
                .password("password123").build();

        when(playerRepository.existsByEmail("montse@gmail.com")).thenReturn(false);
        when(playerRepository.existsByName("Montse")).thenReturn(false);

        /**
         * Act -> acció o comportament que testegem
         */
        RegisterDTO resultEmail = authenticationService.validateRegisterEmail(registerDTO);
        RegisterDTO resultName = authenticationService.validateRegisterName(registerDTO);

        /**
         * Assert -> verificar la sortida
         */
        assertNotNull(resultEmail);
        assertNotNull(resultName);
        assertEquals("Montse", resultName.getName());
        assertEquals("montse@gmail.com", resultEmail.getEmail());

        verify(playerRepository, times(1)).existsByEmail("montse@gmail.com");
        verify(playerRepository, times(1)).existsByName("Montse");
    }

    @DisplayName("Test to validate whether exceptions are thrown when email and name are duplicated")
    @Test
    void testValidateRegisterEmailAndNameThrowsExceptions() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        RegisterDTO registerDTO = RegisterDTO.builder()
                .name("Montse")
                .email("montse@gmail.com")
                .password("password123").build();

        when(playerRepository.existsByEmail("montse@gmail.com")).thenReturn(true);
        when(playerRepository.existsByName("Montse")).thenReturn(true);

        /**
         * Act -> acció o comportament que testegem
         */
        assertThrows(EmailDuplicatedException.class, () -> authenticationService.validateRegisterEmail(registerDTO));
        assertThrows(PlayerDuplicatedException.class, () -> authenticationService.validateRegisterName(registerDTO));

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepository, times(1)).existsByEmail("montse@gmail.com");
        verify(playerRepository, times(1)).existsByName("Montse");
    }

    @DisplayName("Test to check if the unknown name is created correctly")
    @Test
    void testCreateUnknownName() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        RegisterDTO registerDTO = RegisterDTO.builder()
                .email("montse@gmail.com")
                .password("password123").build();

        /**
         * Act -> acció o comportament que testegem
         */
        RegisterDTO result = authenticationService.createUnknownName(registerDTO);

        /**
         * Assert -> verificar la sortida
         */
        assertNotNull(result);
        assertEquals("unknown", result.getName());
    }

    @DisplayName("Test to assign a role to a player")
    @Test
    void testAssignRoleToPlayer() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Role role = new Role("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));

        /**
         * Act -> acció o comportament que testegem
         */
        Role assignedRole = authenticationService.assignRoleToPlayer();

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(role, assignedRole);
        verify(roleRepository, times(1)).findByName("USER");
        verify(roleRepository, times(0)).save(role);
    }

    @DisplayName("Test to register a player in the database")
    @Test
    void testCreateRegister() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        RegisterDTO registerDTO = RegisterDTO.builder()
                .name("Montse")
                .email("montse@gmail.com")
                .password("password123").build();

        Player player = new Player("Montse", "montse@gmail.com", "password123");

        when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn(player.getPassword());
        when(playerRepository.save(any(Player.class))).thenReturn(player);

        /**
         * Act -> acció o comportament que testegem
         */
        RegisterPlayerDTO registerPlayerDTO = authenticationService.createRegister(registerDTO);

        /**
         * Assert -> verificar la sortida
         */
        assertNotNull(registerPlayerDTO);
        assertEquals(registerDTO.getName(), registerPlayerDTO.getName());
        assertEquals(registerDTO.getEmail(), registerPlayerDTO.getEmail());
        assertEquals(registerDTO.getPassword(), registerPlayerDTO.getPassword());
        assertNotNull(registerPlayerDTO.getRegistration());
        verify(playerRepository).save(any(Player.class));
    }

    @DisplayName("Test to find an email in the database")
    @Test
    void testFindEmailInDB() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("montse@gmail.com");

        Player player = new Player();
        player.setEmail("montse@gmail.com");

        RegisterPlayerDTO expectedRegisterPlayerDTO = new RegisterPlayerDTO();
        expectedRegisterPlayerDTO.setEmail("montse@gmail.com");

        when(playerRepository.findByEmail("montse@gmail.com")).thenReturn(Optional.of(player));

        /**
         * Act -> acció o comportament que testegem
         */
        RegisterPlayerDTO actualRegisterPlayerDTO = authenticationService.findEmailInDB(loginDTO);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expectedRegisterPlayerDTO, actualRegisterPlayerDTO);
        verify(playerRepository).findByEmail("montse@gmail.com");
    }

    @DisplayName("Test to check if the exception is thrown when the email is not found in the database")
    @Test
    void testFindEmailInDBThrowsPlayerNotFoundException() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("montse@gmail.com");

        when(playerRepository.findByEmail("montse@gmail.com")).thenReturn(Optional.empty());

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> authenticationService.findEmailInDB(loginDTO));

        /**
         * Assert -> verificar la sortida
         */
        assertEquals("Player's email not found or incorrect", exception.getMessage());
    }

    @DisplayName("Test to validate the password with the use of password encoder")
    @Test
    void testValidatePassword() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("montse@gmail.com");
        loginDTO.setPassword("password123");

        Player player = new Player();
        player.setEmail("montse@gmail.com");
        player.setPassword("password123");

        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setEmail("montse@gmail.com");
        registerPlayerDTO.setPassword("password123");

        when(playerRepository.findByEmail("montse@gmail.com")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("password123", "password123")).thenReturn(true);

        /**
         * Act -> acció o comportament que testegem
         */
        LoginDTO result = authenticationService.validateLoginEmailAndPassword(loginDTO);

        /**
        * Assert -> verificar la sortida
        */
        assertEquals(loginDTO, result);
    }

    @DisplayName("Test to check if the exception is thrown when the password is incorrect")
    @Test
    void testValidatePasswordThrowsException() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("montse@gmail.com");
        loginDTO.setPassword("password123");

        Player player = new Player();
        player.setEmail("montse@gmail.com");
        player.setPassword("password123");

        RegisterPlayerDTO registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setEmail("montse@gmail.com");
        registerPlayerDTO.setPassword("password123");

        when(playerRepository.findByEmail("montse@gmail.com")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("password123", "password123")).thenReturn(false);

        /**
         * Act -> acció o comportament que testegem
         */
        PasswordIncorrectException exception = assertThrows(PasswordIncorrectException.class, () -> authenticationService.validateLoginEmailAndPassword(loginDTO));

        /**
         * Assert -> verificar la sortida
         */
        assertEquals("Password incorrect, please try again", exception.getMessage());
    }

    @DisplayName("Test to validate the login of the player")
    @Test
    void testAuthenticateRegister() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setEmail("montse@gmail.com");
        loginDTO.setPassword("password123");

        Player player = new Player();
        player.setEmail("montse@gmail.com");
        player.setPassword(passwordEncoder.encode("password123"));

        when(playerRepository.findByEmail("montse@gmail.com")).thenReturn(Optional.of(player));
        when(passwordEncoder.matches("password123", player.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);

        String token = "some_token";
        when(jwtGenerator.generateToken(authentication)).thenReturn(token);
        when(jwtGenerator.isTokenValid(token)).thenReturn(true);

        /**
         * Act -> acció o comportament que testegem
         */
        AuthenticationResponseDTO result = authenticationService.authenticateRegister(loginDTO);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(loginDTO.getEmail(), result.getEmail());
        assertEquals(token, result.getAccessToken());
        assertTrue(passwordEncoder.matches(loginDTO.getPassword(), result.getPassword()));
    }

}

