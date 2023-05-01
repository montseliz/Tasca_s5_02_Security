package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.service.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Game;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.GameDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.PlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.GamesNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IGameRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IPlayerRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.AuthenticationService;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql.DiceGameServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiceGameServiceTests {

    @Mock
    private IPlayerRepository playerRepository;
    @Mock
    private IGameRepository gameRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private DiceGameServiceImpl diceGameService;

    private Role role;
    private Player player1;
    private Player player2;
    private List<Player> players;

    /**
     * Arrange -> condició prèvia o configuració
     */
    @BeforeEach
    void setup() {
        role = new Role("USER");

        players = new ArrayList<>();

        player1 = new Player();
        player1.setId(1L);
        player1.setName("Montse");
        player2 = new Player();
        player2.setId(2L);
        player2.setName("Anna");

        Game game1 = new Game();
        game1.setResult(Game.ResultGame.WINNER);
        Game game2 = new Game();
        game2.setResult(Game.ResultGame.LOSER);
        Game game3 = new Game();
        game3.setResult(Game.ResultGame.WINNER);
        Game game4 = new Game();
        game4.setResult(Game.ResultGame.WINNER);

        player1.addGamesHistory(game1);
        player2.addGamesHistory(game2);
        player2.addGamesHistory(game3);
        player2.addGamesHistory(game4);

        players.add(player1);
        players.add(player2);
    }

    @DisplayName("Test to retrieve a player by its id from the database")
    @Test
    void testGetPlayerById() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player expectedPlayer = Player.builder()
                .id(1L)
                .name("Montse")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("montse@gmail.com")
                .password("password123")
                .role(role).build();

        when(playerRepository.findById(1L)).thenReturn(Optional.of(expectedPlayer));

        /**
         * Act -> acció o comportament que testegem
         */
        Player actualPlayer = diceGameService.getPlayerById(1L);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expectedPlayer, actualPlayer);
        verify(playerRepository).findById(1L);
    }

    @DisplayName("Test to validate that the exception is thrown if the Player is not found by id")
    @Test
    void testGetPlayerByIdThrowsException() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player expectedPlayer = Player.builder()
                .id(1L)
                .name("Montse")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("montse@gmail.com")
                .password("password123")
                .role(role).build();

        when(playerRepository.findById(1L)).thenReturn(Optional.empty());

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> diceGameService.getPlayerById(expectedPlayer.getId()));

        /**
         * Assert -> verificar la sortida
         */
        assertEquals("Player with ID " + expectedPlayer.getId() + " not found", exception.getMessage());
    }

    @DisplayName("Test to retrieve all players from the database")
    @Test
    void testGetListPlayers() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player expectedPlayer = Player.builder()
                .id(1L)
                .name("Montse")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("montse@gmail.com")
                .password("password123")
                .role(role).build();

        Player expectedPlayer2 = Player.builder()
                .id(2L)
                .name("Anna")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("anna@gmail.com")
                .password("password456")
                .role(new Role("USER")).build();

        List<Player> expectedPlayers = List.of(expectedPlayer, expectedPlayer2);

        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        /**
         * Act -> acció o comportament que testegem
         */
        List<Player> actualPlayers = diceGameService.getListPlayers();

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expectedPlayers, actualPlayers);
    }

    @DisplayName("Test to validate that the exception is thrown when no players are found in the database")
    @Test
    void testGetListPlayersThrowsException() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        List<Player> expectedPlayers = List.of();

        when(playerRepository.findAll()).thenReturn(expectedPlayers);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> diceGameService.getListPlayers());

        /**
         * Assert -> verificar la sortida
         */
        assertEquals("There are no players introduced in the database", exception.getMessage());
    }

    @DisplayName("Test to validate the conversion from Player to DTO")
    @Test
    void testConvertPlayerToDTO() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        MockitoAnnotations.openMocks(this); //Important perquè funcioni el modelMapper
        Player player = new Player();
        player.setName("Montse");
        player.setRegistration(LocalDateTime.now());

        PlayerDTO expectedPlayerDTO = new PlayerDTO();
        expectedPlayerDTO.setName("Montse");
        expectedPlayerDTO.setRegistration(player.getRegistration());

        when(modelMapper.map(player, PlayerDTO.class)).thenReturn(expectedPlayerDTO);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerDTO result = diceGameService.convertPlayerToDTO(player);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expectedPlayerDTO, result);
        verify(modelMapper).map(player, PlayerDTO.class);
    }

    @DisplayName("Test to validate the conversion from Game to DTO")
    @Test
    void testConvertGameToDTO() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        MockitoAnnotations.openMocks(this); //Important perquè funcioni el modelMapper
        Player player = new Player();

        Game game = new Game();
        game.setDice1((short) 4);
        game.setDice2((short) 3);
        game.setResult(Game.ResultGame.WINNER);
        game.setPlayer(player);

        GameDTO expectedGameDTO = new GameDTO();
        expectedGameDTO.setDice1((short) 4);
        expectedGameDTO.setDice2((short) 3);
        expectedGameDTO.setResult(GameDTO.ResultGame.WINNER);
        expectedGameDTO.setNamePlayer(player.getName());

        when(modelMapper.map(game, GameDTO.class)).thenReturn(expectedGameDTO);

        /**
         * Act -> acció o comportament que testegem
         */
        GameDTO result = diceGameService.convertGameToDTO(game);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expectedGameDTO, result);
        verify(modelMapper).map(game, GameDTO.class);
    }

    @DisplayName("Test to validate the conversion from Game List to DTO")
    @Test
    void testConvertGameListToDTO() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        MockitoAnnotations.openMocks(this); //Important perquè funcioni el modelMapper
        Player player = new Player();

        List<Game> games = new ArrayList<>();
        games.add(Game.builder().id(1L).dice1((short) 4).dice2((short) 3).result(Game.ResultGame.WINNER).player(player).build());
        games.add(Game.builder().id(2L).dice1((short) 2).dice2((short) 6).result(Game.ResultGame.LOSER).player(player).build());

        List<GameDTO> expectedDTOs = games.stream().map(diceGameService::convertGameToDTO).toList();

        /**
         * Act -> acció o comportament que testegem
         */
        List<GameDTO> actualDTOs = diceGameService.convertGameListToDTO(games);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expectedDTOs.size(), actualDTOs.size());
        assertEquals(expectedDTOs.get(0).getId(), actualDTOs.get(0).getId());
        assertEquals(expectedDTOs.get(1).getId(), actualDTOs.get(1).getId());
    }

    @DisplayName("Test to update a player's name in the database")
    @Test
    void testEditPlayer() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player = new Player();
        player.setId(1L);
        player.setName("Montse");

        RegisterDTO registerDTO = new RegisterDTO();
        registerDTO.setName("Anna");

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findById(player.getId())).thenReturn(Optional.of(player));
        when(playerRepositoryMock.save(player)).thenReturn(player);
        when(playerRepositoryMock.existsById(player.getId())).thenReturn(true);

        AuthenticationService authenticationServiceMock = mock(AuthenticationService.class);
        when(authenticationServiceMock.validateRegisterName(registerDTO)).thenReturn(registerDTO);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepository, authenticationServiceMock);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerDTO updatedPlayer = diceGameService.editPlayer(player.getId(), registerDTO);

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(registerDTO.getName(), updatedPlayer.getName());
    }

    @DisplayName("Test to get player with win percentatge from the database")
    @Test
    void testGetPlayersWithWinPercentage() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findAll()).thenReturn(players);
        when(playerRepositoryMock.existsById(player1.getId())).thenReturn(true);
        when(playerRepositoryMock.existsById(player2.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepository, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        List<PlayerDTO> result = diceGameService.getPlayersWithWinPercentage();

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(2, result.size());
        assertEquals("Montse", result.get(0).getName());
        assertEquals("100,00", result.get(0).getWinPercentage());
        assertEquals("Anna", result.get(1).getName());
        assertEquals("66,67", result.get(1).getWinPercentage());
    }

    @DisplayName("Test to get winning average from all players who played")
    @Test
    void testGetWinningAverage() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        IGameRepository gameRepositoryMock = mock(IGameRepository.class);
        when(gameRepositoryMock.isEmpty()).thenReturn(false);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findAll()).thenReturn(players);
        when(playerRepositoryMock.existsById(player1.getId())).thenReturn(true);
        when(playerRepositoryMock.existsById(player2.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepositoryMock, authenticationService);

        String expected = "Players average wins: 83,34";

        /**
         * Act -> acció o comportament que testegem
         */
        String result = diceGameService.getWinningAverage();

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(expected, result);
    }

    @DisplayName("Test to get the most loser player who played")
    @Test
    void testGetMostLoser() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        IGameRepository gameRepositoryMock = mock(IGameRepository.class);
        when(gameRepositoryMock.isEmpty()).thenReturn(false);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findAll()).thenReturn(players);
        when(playerRepositoryMock.existsById(player1.getId())).thenReturn(true);
        when(playerRepositoryMock.existsById(player2.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepositoryMock, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerDTO mostLoser = diceGameService.getMostLoser();

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock, times(2)).findAll();
        verify(playerRepositoryMock, times(2)).existsById(anyLong());
        assertEquals("Anna", mostLoser.getName());
        assertEquals("66,67", mostLoser.getWinPercentage());
    }

    @DisplayName("Test to get the most winner player who played")
    @Test
    void testGetMostWinner() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        IGameRepository gameRepositoryMock = mock(IGameRepository.class);
        when(gameRepositoryMock.isEmpty()).thenReturn(false);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findAll()).thenReturn(players);
        when(playerRepositoryMock.existsById(player1.getId())).thenReturn(true);
        when(playerRepositoryMock.existsById(player2.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepositoryMock, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerDTO mostWinner = diceGameService.getMostWinner();

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock, times(2)).findAll();
        verify(playerRepositoryMock, times(2)).existsById(anyLong());
        assertEquals("Montse", mostWinner.getName());
        assertEquals("100,00", mostWinner.getWinPercentage());
    }

    @DisplayName("Test to create a game in the database")
    @Test
    void testCreateGame() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player = new Player();
        player.setId(1L);
        player.setName("Anna");

        Game game = new Game();
        game.setId(1L);
        game.setDice1((short)5);
        game.setDice2((short)2);
        game.setResult(Game.ResultGame.WINNER);
        game.setPlayer(player);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findById(player.getId())).thenReturn(Optional.of(player));

        IGameRepository gameRepositoryMock = mock(IGameRepository.class);
        when(gameRepositoryMock.save(any(Game.class))).thenReturn(game);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepositoryMock, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        GameDTO gameDTO = diceGameService.createGame(player.getId());

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock).findById(player.getId());
        verify(gameRepositoryMock).save(any(Game.class));
        assertNotNull(gameDTO);
        assertEquals(player.getName(), gameDTO.getNamePlayer());
    }

    @DisplayName("Test to remove games by player in the database")
    @Test
    void testRemoveGamesByPlayer() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        List<Game> games = new ArrayList<>();
        Game game1 = new Game();
        game1.setId(1L);
        game1.setResult(Game.ResultGame.WINNER);
        games.add(game1);

        Player player = new Player();
        player.setId(1L);
        player.setName("Xavi");
        player.setGamesHistory(games);

        IGameRepository gameRepositoryMock = mock(IGameRepository.class);
        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findById(player.getId())).thenReturn(Optional.of(player));

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepositoryMock, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        diceGameService.removeGamesByPlayer(player.getId());

        /**
         * Assert -> verificar la sortida
         */
        assertEquals(0, player.getGamesHistory().size());
    }

    @DisplayName("Test to get games history by player")
    @Test
    void testGetGamesHistoryByPlayer() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        List<Game> games = new ArrayList<>();

        Game game1 = new Game();
        game1.setId(1L);
        game1.setResult(Game.ResultGame.WINNER);

        games.add(game1);

        Player player = new Player();
        player.setId(1L);
        player.setName("Xavi");
        player.setGamesHistory(games);

        MockitoAnnotations.openMocks(this);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findById(player.getId())).thenReturn(Optional.of(player));

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepository, authenticationService);

        List<GameDTO> expectedGameDTOs = diceGameService.convertGameListToDTO(games);

        /**
         * Act -> acció o comportament que testegem
         */
        List<GameDTO> actualGameDTOs = diceGameService.getGamesHistoryByPlayer(player.getId());

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock).findById(player.getId());
        assertEquals(expectedGameDTOs, actualGameDTOs);
    }

    @DisplayName("Test to obtain the win percentatge of the player")
    @Test
    void testObtainWinPercentage() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player = new Player();
        player.setId(1L);
        player.setName("Montse");

        Game game1 = new Game();
        game1.setResult(Game.ResultGame.WINNER);
        Game game2 = new Game();
        game2.setResult(Game.ResultGame.LOSER);

        player.addGamesHistory(game1);
        player.addGamesHistory(game2);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.existsById(player.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepository, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerDTO playerDTO = diceGameService.obtainWinPercentage(player);

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock, times(1)).existsById(player.getId());
        assertEquals(player.getName(), playerDTO.getName());
        assertEquals("50,00", playerDTO.getWinPercentage());
    }

    @DisplayName("Test to validate if exists a player in the database")
    @Test
    void testIfExistsPlayer() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player = new Player();
        player.setId(1L);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.existsById(player.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper,playerRepositoryMock, gameRepository, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        Player result = diceGameService.ifExistsPlayer(player);

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock).existsById(player.getId());
        assertEquals(player, result);
    }

    @DisplayName("Test to validate if the exception is thrown when a player doesn't exists in the database")
    @Test
    void testIfExistsPlayerThrowsExceptions() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player = new Player();
        player.setId(1L);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.existsById(player.getId())).thenReturn(false);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper,playerRepositoryMock, gameRepository, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        PlayerNotFoundException exception = assertThrows(PlayerNotFoundException.class, () -> diceGameService.ifExistsPlayer(player));

        /**
         * Assert -> verificar la sortida
         */
        assertEquals("Player not found in the database", exception.getMessage());
    }

    @DisplayName("Test to validate if exists games by player in the database")
    @Test
    void testIfExistsGames() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Game game = new Game();
        game.setId(1L);
        Game game2 = new Game();
        game2.setId(2L);

        Player player = new Player();
        player.setId(1L);
        player.setGamesHistory(List.of(game, game2));

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findById(player.getId())).thenReturn(Optional.of(player));

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper,playerRepositoryMock, gameRepository, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        List<Game> result = diceGameService.ifExistsGames(player.getId());

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock).findById(player.getId());
        assertEquals(player.getGamesHistory(), result);
    }

    @DisplayName("Test to validate if the exception is thrown when games by player doesn't exists in the database")
    @Test
    void testIfExistsGamesThrowsExceptions() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player = new Player();
        player.setId(1L);
        player.setName("Anna");
        player.setGamesHistory(List.of());

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findById(player.getId())).thenReturn(Optional.of(player));

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper,playerRepositoryMock, gameRepository, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        GamesNotFoundException exception = assertThrows(GamesNotFoundException.class, () -> diceGameService.ifExistsGames(player.getId()));

        /**
         * Assert -> verificar la sortida
         */
        assertEquals("There are no games played by " + player.getName(), exception.getMessage());
    }

    @DisplayName("Test to validate the players who played games")
    @Test
    void testPlayersWhoPlayed() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        List<Player> players = new ArrayList<>();

        Player player1 = new Player();
        player1.setId(1L);
        player1.setName("Montse");

        Game game1 = new Game();
        game1.setResult(Game.ResultGame.WINNER);

        player1.addGamesHistory(game1);
        players.add(player1);

        Player player2 = new Player();
        player2.setId(2L);
        player2.setName("Anna");
        players.add(player2);

        IGameRepository gameRepositoryMock = mock(IGameRepository.class);
        when(gameRepositoryMock.isEmpty()).thenReturn(false);

        IPlayerRepository playerRepositoryMock = mock(IPlayerRepository.class);
        when(playerRepositoryMock.findAll()).thenReturn(players);
        when(playerRepositoryMock.existsById(player1.getId())).thenReturn(true);
        when(playerRepositoryMock.existsById(player2.getId())).thenReturn(true);

        DiceGameServiceImpl diceGameService = new DiceGameServiceImpl(modelMapper, playerRepositoryMock, gameRepositoryMock, authenticationService);

        /**
         * Act -> acció o comportament que testegem
         */
        List<PlayerDTO> result = diceGameService.playersWhoPlayed();

        /**
         * Assert -> verificar la sortida
         */
        verify(playerRepositoryMock, times(1)).existsById(player1.getId());
        verify(playerRepositoryMock, times(1)).existsById(player2.getId());
        verify(gameRepositoryMock, times(1)).isEmpty();
        assertEquals(1, result.size());
        assertEquals("Montse", result.get(0).getName());
        assertEquals("100,00", result.get(0).getWinPercentage());
    }

}
