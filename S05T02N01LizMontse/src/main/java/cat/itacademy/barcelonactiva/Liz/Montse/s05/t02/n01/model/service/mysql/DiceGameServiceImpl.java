package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Game;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.GameDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.PlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.GamesNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IGameRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IPlayerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Game.ResultGame.WINNER;

@Service
public class DiceGameServiceImpl implements IPlayerService, IGameService {

    private final ModelMapper modelMapper;
    private final IPlayerRepository playerRepository;
    private final IGameRepository gameRepository;
    private final AuthenticationService authenticationService;

    @Autowired
    public DiceGameServiceImpl(ModelMapper modelMapper, IPlayerRepository playerRepository, IGameRepository gameRepository, AuthenticationService authenticationService) {
        super();
        this.modelMapper = modelMapper;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.authenticationService = authenticationService;
    }

    //region CONVERTERS
    public PlayerDTO convertPlayerToDTO (Player player) {
        return modelMapper.map(player, PlayerDTO.class);
    }

    public GameDTO convertGameToDTO (Game game) {
        return modelMapper.map(game, GameDTO.class);
    }

    public List<GameDTO> convertGameListToDTO(List<Game> gamesHistory) {
        return gamesHistory.stream().map(this::convertGameToDTO).collect(Collectors.toList());
    }
    //endregion CONVERTERS

    @Override
    public Player getPlayerById(long id) {
        Optional<Player> player = playerRepository.findById(id);
        if(player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException("Player with ID " + id + " not found");
        }
    }

    @Override
    public List<Player> getListPlayers() {
        if (playerRepository.findAll().isEmpty()) {
            throw new PlayerNotFoundException("There are no players introduced in the database");
        } else {
            return playerRepository.findAll();
        }
    }

    @Override
    public PlayerDTO editPlayer(long id, RegisterDTO registerDTO) {
        Player playerToUpdate = getPlayerById(id);

        RegisterDTO registerNameValidated = authenticationService.validateRegisterName(registerDTO);
        playerToUpdate.setName(registerNameValidated.getName());

        Player playerUpdated = playerRepository.save(playerToUpdate);

        return obtainWinPercentage(playerUpdated);
    }

    @Override
    public List<PlayerDTO> getPlayersWithWinPercentage() {
        List<Player> allPlayers = getListPlayers();
        List<PlayerDTO> allPlayersDTO = new ArrayList<>();

        allPlayers.forEach(p -> allPlayersDTO.add(obtainWinPercentage(p)));
        return allPlayersDTO;
    }

    @Override
    public String getWinningAverage() {
        List<PlayerDTO> playersWhoPlayed = playersWhoPlayed();

        double totalWinPercentage = playersWhoPlayed.stream().mapToDouble(p -> Double.parseDouble(p.getWinPercentage().replace(",", "."))).sum();

        double winningAverage = totalWinPercentage / playersWhoPlayed.size();

        return "Players average wins: " + String.format("%.2f", winningAverage);
    }

    @Override
    public PlayerDTO getMostLoser() {
        List<PlayerDTO> playersWhoPlayed = playersWhoPlayed();

        return playersWhoPlayed.stream().min(Comparator.comparingDouble(p -> Double.parseDouble(p.getWinPercentage().replace(",", ".")))).get();
    }

    @Override
    public PlayerDTO getMostWinner() {
        List<PlayerDTO> playersWhoPlayed = playersWhoPlayed();

        return playersWhoPlayed.stream().max(Comparator.comparingDouble(p -> Double.parseDouble(p.getWinPercentage().replace(",", ".")))).get();
    }

    @Override
    public GameDTO createGame(long id) {
        Player playerById = getPlayerById(id);

        Game newGame = new Game(playerById);
        playerById.addGamesHistory(newGame);
        gameRepository.save(newGame);

        return convertGameToDTO(newGame);
    }

    /**
     * Anotació @Transactional indispensable perquè el mètode elimini tots els games del player especificat de la base de dades.
     * És necessària perquè l'eliminació de tots els jocs de l'historial de jocs d'un jugador és una operació que pot afectar a
     * múltiples registres en la base de dades.
     * Utilitzant l'anotació garantim que l'operació d'eliminar es realitzi de manera atòmica, que significa que
     * es compromet o es desfà en cas que es produeixi una excepció.
     * Així s'evita que es faci una eliminació parcial o incompleta que podria deixar la base de dades en un estat inconsistent.
     * Per tant, és indispensable perquè l'eliminació es dugui a terme de manera segura i consistent en la base de dades.
     */
    @Override
    @Transactional
    public void removeGamesByPlayer(long id) {
        List<Game> gamesToRemove = ifExistsGames(id);

        gamesToRemove.clear();
    }

    @Override
    public List<GameDTO> getGamesHistoryByPlayer(long id) {

        return convertGameListToDTO(ifExistsGames(id));
    }

    /**
     * Mètode encarregat de calcular el percentatge de victòries del Player.
     * S'utilitza en el mètode editPlayer() i getPlayersWithWinPercentage().
     */
    public PlayerDTO obtainWinPercentage(Player player) {
        Player playerValidated = ifExistsPlayer(player);
        PlayerDTO playerDTOToReturn = convertPlayerToDTO(playerValidated);

        if (playerValidated.getGamesHistory().isEmpty()) {
            playerDTOToReturn.setWinPercentage("Cannot calculate a player's winning percentage with no games played");
        } else {
            long totalGames = playerValidated.getGamesHistory().size();
            long totalWins = playerValidated.getGamesHistory().stream().filter(g -> g.getResult() == WINNER).count();
            double winPercentage = ((double) totalWins / totalGames) * 100.0d;

            playerDTOToReturn.setWinPercentage(String.format("%.2f", winPercentage));
        }
        return playerDTOToReturn;
    }

    /**
     * Mètode per validar l'existència del Player a la base de dades.
     * S'utilitza en el mètode obtainWinPercentage().
     */
    public Player ifExistsPlayer(Player player) {
        if (playerRepository.existsById(player.getId())) {
            return player;
        } else {
            throw new PlayerNotFoundException("Player not found in the database");
        }
    }

    /**
     * Mètode per validar l'existència de games per player a la base de dades.
     * S'utilitza en els mètodes removeGamesByPlayer() i getGamesHistoryByPlayer().
     */
    public List<Game> ifExistsGames(long id) {
        Player playerById = getPlayerById(id);
        if (playerById.getGamesHistory().isEmpty()) {
            throw new GamesNotFoundException("There are no games played by " + playerById.getName());
        } else {
            return playerById.getGamesHistory();
        }
    }

    /**
     * Mètode per obtenir només els jugadors que han jugat.
     * S'utilitza en els mètodes getWinningAverage(), getMostLoser() i getMostWinner().
     */
    public List<PlayerDTO> playersWhoPlayed() {
        List<PlayerDTO> allPlayersDTO = getPlayersWithWinPercentage();

        if (gameRepository.isEmpty()) {
            throw new GamesNotFoundException("There are no games stored in the database");
        } else {
            return allPlayersDTO.stream().filter(p -> !p.getWinPercentage().equals("Cannot calculate a player's winning percentage with no games played")).collect(Collectors.toList());
        }
    }

}