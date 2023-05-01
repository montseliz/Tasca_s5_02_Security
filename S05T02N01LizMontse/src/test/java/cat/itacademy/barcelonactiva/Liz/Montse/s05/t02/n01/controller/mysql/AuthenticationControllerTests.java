package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.controller.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.AuthenticationResponseDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.LoginDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterPlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTests {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private RegisterDTO registerDTO;
    private RegisterPlayerDTO registerPlayerDTO;
    private LoginDTO loginDTO;
    private AuthenticationResponseDTO authenticationResponseDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();

        registerDTO = new RegisterDTO();
        registerDTO.setName("Montse");
        registerDTO.setEmail("montse@gmail.com");
        registerDTO.setPassword("password123");

        registerPlayerDTO = new RegisterPlayerDTO();
        registerPlayerDTO.setId(1L);
        registerPlayerDTO.setName(registerDTO.getName());
        registerPlayerDTO.setEmail(registerDTO.getEmail());

        loginDTO = new LoginDTO();
        loginDTO.setEmail("montse@gmail.com");
        loginDTO.setPassword("password123");

        authenticationResponseDTO = new AuthenticationResponseDTO();
        authenticationResponseDTO.setEmail("montse@gmail.com");
        authenticationResponseDTO.setPassword("password123");
        authenticationResponseDTO.setTokenType("Bearer");
        authenticationResponseDTO.setAccessToken("testToken");
    }

    @DisplayName("Test to register a player with success")
    @Test
    void testRegister() throws Exception {

        given(authenticationService.createRegister(registerDTO)).willReturn(registerPlayerDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(registerDTO.getName()))
                .andExpect(jsonPath("$.email").value(registerDTO.getEmail()));

        verify(authenticationService, times(1)).createRegister(registerDTO);
    }

    @DisplayName("Test to login a player with valid credentials")
    @Test
    void testLogin() throws Exception {

        given(authenticationService.authenticateRegister(loginDTO)).willReturn(authenticationResponseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(loginDTO.getEmail()))
                .andExpect(jsonPath("$.password").value(loginDTO.getPassword()))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("testToken"));

        verify(authenticationService, times(1)).authenticateRegister(loginDTO);
    }

}