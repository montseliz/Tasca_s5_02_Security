package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.controller.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.GameDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.PlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.IGameService;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.IPlayerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DiceGameControllerTests {

    private MockMvc mockMvc;

    @Mock
    private IPlayerService playerService;

    @Mock
    private IGameService gameService;

    @InjectMocks
    private DiceGameControllerImpl diceGameController;

    private Player player;
    private PlayerDTO playerDTO;
    private PlayerDTO playerDTO2;
    private GameDTO gameDTO;
    private GameDTO gameDTO2;
    private RegisterDTO registerDTO;
    private List<PlayerDTO> playersDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(diceGameController).build();

        Role role = new Role("USER");

        player = new Player(1L, "Montse", LocalDateTime.now(), new ArrayList<>(), "montse@gmail.com", "password123", role);
        Player player2 = new Player(2L, "Anna", LocalDateTime.now(), new ArrayList<>(), "anna@gmail.com", "password456", role);

        gameDTO = new GameDTO();
        gameDTO.setId(1L);
        gameDTO.setDice1((short) 6);
        gameDTO.setDice2((short) 1);
        gameDTO.setResult(GameDTO.ResultGame.WINNER);
        gameDTO.setNamePlayer(player.getName());

        gameDTO2 = new GameDTO();
        gameDTO2.setId(2L);
        gameDTO2.setDice1((short) 4);
        gameDTO2.setDice2((short) 3);
        gameDTO2.setResult(GameDTO.ResultGame.WINNER);
        gameDTO2.setNamePlayer(player.getName());

        GameDTO gameDTO3 = new GameDTO();
        gameDTO3.setId(3L);
        gameDTO3.setDice1((short) 5);
        gameDTO3.setDice2((short) 4);
        gameDTO3.setResult(GameDTO.ResultGame.LOSER);
        gameDTO2.setNamePlayer(player2.getName());

        registerDTO = new RegisterDTO("Montserrat", "montse@gmail.com", "password123");

        playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setName(registerDTO.getName());
        playerDTO.setWinPercentage("100,00");

        playerDTO2 = new PlayerDTO();
        playerDTO2.setId(2L);
        playerDTO2.setName("Anna");
        playerDTO2.setWinPercentage("0,00");

        playersDTO = new ArrayList<>(List.of(playerDTO, playerDTO2));
    }

    @DisplayName("Test to validate the endpoint PUT players/update/{id}")
    @Test
    void testUpdatePlayer() throws Exception {
        given(playerService.editPlayer(player.getId(), registerDTO)).willReturn(playerDTO);

        mockMvc.perform(put("/players/update/{id}", player.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerDTO.getId()))
                .andExpect(jsonPath("$.name").value(playerDTO.getName()))
                .andExpect(jsonPath("$.registration").value(playerDTO.getRegistration()))
                .andExpect(jsonPath("$.winPercentage").value(playerDTO.getWinPercentage()));

        verify(playerService, times(1)).editPlayer(player.getId(), registerDTO);
    }

    @DisplayName("Test to validate the endpoint POST players/{id}/game")
    @Test
    void testNewGame() throws Exception {
        given(gameService.createGame(player.getId())).willReturn(gameDTO);

        mockMvc.perform(post("/players/{id}/game", player.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(gameDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dice1").value(6))
                .andExpect(jsonPath("$.dice2").value(1))
                .andExpect(jsonPath("$.result").value(gameDTO.getResult().toString()))
                .andExpect(jsonPath("$.namePlayer").value(gameDTO.getNamePlayer()));

        verify(gameService, times(1)).createGame(player.getId());
    }

    @DisplayName("Test to validate the endpoint DELETE players/{id}/games")
    @Test
    void testDeleteGames() throws Exception {
        doNothing().when(gameService).removeGamesByPlayer(player.getId());

        mockMvc.perform(delete("/players/{id}/games", player.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.textMessage").value("Games history removed successfully"));

        verify(gameService, times(1)).removeGamesByPlayer(player.getId());
    }

    @DisplayName("Test to validate the endpoint GET players/")
    @Test
    void testGetAllPlayers() throws Exception {

        given(playerService.getPlayersWithWinPercentage()).willReturn(playersDTO);

        mockMvc.perform(get("/players/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(playersDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(playersDTO.size())))
                .andExpect(jsonPath("$[0].id").value(playersDTO.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(playersDTO.get(0).getName()))
                .andExpect(jsonPath("$[0].winPercentage").value(playersDTO.get(0).getWinPercentage()))
                .andExpect(jsonPath("$[1].id").value(playersDTO.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(playersDTO.get(1).getName()))
                .andExpect(jsonPath("$[1].winPercentage").value(playersDTO.get(1).getWinPercentage()));

        verify(playerService, times(1)).getPlayersWithWinPercentage();
    }

    @DisplayName("Test to validate the endpoint GET players/{id}/games")
    @Test
    void testGetAllGamesByPlayer() throws Exception {

        given(gameService.getGamesHistoryByPlayer(player.getId()))
                .willReturn(List.of(gameDTO, gameDTO2));

        mockMvc.perform(get("/players/{id}/games", player.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of(gameDTO, gameDTO2))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].dice1").value(6))
                .andExpect(jsonPath("$[0].dice2").value(1))
                .andExpect(jsonPath("$[0].result").value(gameDTO.getResult().toString()))
                .andExpect(jsonPath("$[0].namePlayer").value(gameDTO.getNamePlayer()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].dice1").value(4))
                .andExpect(jsonPath("$[1].dice2").value(3))
                .andExpect(jsonPath("$[1].result").value(gameDTO2.getResult().toString()))
                .andExpect(jsonPath("$[1].namePlayer").value(gameDTO2.getNamePlayer()));

        verify(gameService, times(1)).getGamesHistoryByPlayer(player.getId());
    }

    @DisplayName("Test to validate the endpoint GET players/ranking")
    @Test
    void testGetWinAverage() throws Exception {
        given(playerService.getWinningAverage()).willReturn("Players average wins: 66,66%");

        mockMvc.perform(get("/players/ranking")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.textMessage").value("Players average wins: 66,66%"));

        verify(playerService, times(1)).getWinningAverage();
    }

    @DisplayName("Test to validate the endpoint GET players/ranking/loser")
    @Test
    void testGetLoser() throws Exception {
        given(playerService.getMostLoser()).willReturn(playerDTO2);

        mockMvc.perform(get("/players/ranking/loser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(playerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerDTO2.getId()))
                .andExpect(jsonPath("$.name").value(playerDTO2.getName()))
                .andExpect(jsonPath("$.registration").value(playerDTO2.getRegistration()))
                .andExpect(jsonPath("$.winPercentage").value(playerDTO2.getWinPercentage()));

        verify(playerService, times(1)).getMostLoser();
    }

    @DisplayName("Test to validate the endpoint GET players/ranking/winner")
    @Test
    void testGetWinner() throws Exception {
        given(playerService.getMostWinner()).willReturn(playerDTO);

        mockMvc.perform(get("/players/ranking/winner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(playerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerDTO.getId()))
                .andExpect(jsonPath("$.name").value(playerDTO.getName()))
                .andExpect(jsonPath("$.registration").value(playerDTO.getRegistration()))
                .andExpect(jsonPath("$.winPercentage").value(playerDTO.getWinPercentage()));

        verify(playerService, times(1)).getMostWinner();
    }
}
