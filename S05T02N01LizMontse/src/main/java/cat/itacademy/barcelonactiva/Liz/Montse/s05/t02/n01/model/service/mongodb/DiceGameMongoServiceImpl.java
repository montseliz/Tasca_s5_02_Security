package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mongodb;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.GameMongo;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.PlayerMongo;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.GameMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.PlayerMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.RegisterMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.GamesNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mongodb.IPlayerMongoRepository;
import jakarta.transaction.Transactional;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiceGameMongoServiceImpl implements IPlayerMongoService, IGameMongoService {

    private final ModelMapper modelMapper;
    private final IPlayerMongoRepository playerRepository;
    private final AuthenticationServiceMongo authenticationServiceMongo;

    @Autowired
    public DiceGameMongoServiceImpl(ModelMapper modelMapper, IPlayerMongoRepository playerRepository, AuthenticationServiceMongo authenticationServiceMongo) {
        super();
        this.modelMapper = modelMapper;
        this.playerRepository = playerRepository;
        this.authenticationServiceMongo = authenticationServiceMongo;
    }

    //region CONVERTERS
    private PlayerMongoDTO convertPlayerToDTO(PlayerMongo player) {
        return modelMapper.map(player, PlayerMongoDTO.class);
    }

    private GameMongoDTO convertGameToDTO(GameMongo game) {
        return modelMapper.map(game, GameMongoDTO.class);
    }

    private List<GameMongoDTO> convertGameListToDTO(List<GameMongo> gamesHistory) {
        return gamesHistory.stream().map(this::convertGameToDTO).collect(Collectors.toList());
    }
    //endregion CONVERTERS

    @Override
    public PlayerMongo getPlayerById(ObjectId id) {
        Optional<PlayerMongo> player = playerRepository.findById(id);
        if(player.isPresent()) {
            return player.get();
        } else {
            throw new PlayerNotFoundException("Player with ID " + id + " not found");
        }
    }

    @Override
    public List<PlayerMongo> getListPlayers() {
        if (playerRepository.findAll().isEmpty()) {
            throw new PlayerNotFoundException("There are no players introduced in the database");
        } else {
            return playerRepository.findAll();
        }
    }

    @Override
    public PlayerMongoDTO editPlayer(ObjectId id, RegisterMongoDTO registerMongoDTO) {
        PlayerMongo playerToUpdate = getPlayerById(id);

        RegisterMongoDTO registerNameValidated = authenticationServiceMongo.validateRegisterName(registerMongoDTO);
        playerToUpdate.setName(registerNameValidated.getName());

        PlayerMongo playerUpdated = playerRepository.save(playerToUpdate);

        return obtainWinPercentage(playerUpdated);
    }

    @Override
    public List<PlayerMongoDTO> getPlayersWithWinPercentage() {
        List<PlayerMongo> allPlayers = getListPlayers();
        List<PlayerMongoDTO> allPlayersDTO = new ArrayList<>();

        allPlayers.forEach(p -> allPlayersDTO.add(obtainWinPercentage(p)));
        return allPlayersDTO;
    }

    @Override
    public String getWinningAverage() {
        List<PlayerMongoDTO> playersWhoPlayed = playersWhoPlayed();

        double totalWinPercentage = playersWhoPlayed.stream().mapToDouble(p -> Double.parseDouble(p.getWinPercentage().replace(",", "."))).sum();

        double winningAverage = totalWinPercentage / playersWhoPlayed.size();

        return "Players average wins: " + String.format("%.2f", winningAverage);
    }

    @Override
    public PlayerMongoDTO getMostLoser() {
        List<PlayerMongoDTO> playersWhoPlayed = playersWhoPlayed();

        return playersWhoPlayed.stream().min(Comparator.comparingDouble(p -> Double.parseDouble(p.getWinPercentage().replace(",", ".")))).get();
    }

    @Override
    public PlayerMongoDTO getMostWinner() {
        List<PlayerMongoDTO> playersWhoPlayed = playersWhoPlayed();

        return playersWhoPlayed.stream().max(Comparator.comparingDouble(p -> Double.parseDouble(p.getWinPercentage().replace(",", ".")))).get();
    }

    @Override
    public GameMongoDTO createGame(ObjectId id) {
        PlayerMongo playerById = getPlayerById(id);

        GameMongo newGame = new GameMongo();
        playerById.addGamesHistory(newGame);
        playerRepository.save(playerById);

        GameMongoDTO newGameDTO = convertGameToDTO(newGame);
        newGameDTO.setNamePlayer(playerById.getName());

        return newGameDTO;
    }

    @Override
    @Transactional
    public void removeGamesByPlayer(ObjectId id) {
        PlayerMongo playerById = getPlayerById(id);

        List<GameMongo> gamesToRemove = ifExistsGames(playerById);
        gamesToRemove.clear();

        playerRepository.save(playerById);
    }

    @Override
    public List<GameMongoDTO> getGamesHistoryByPlayer(ObjectId id) {
        PlayerMongo playerById = getPlayerById(id);

        List<GameMongoDTO> gameListDTOToReturn = convertGameListToDTO(ifExistsGames(playerById));
        gameListDTOToReturn.forEach(g -> g.setNamePlayer(playerById.getName()));

        return gameListDTOToReturn;
    }

    /**
     * Mètode encarregat de calcular el percentatge de victòries del Player.
     * S'utilitza en el mètode editPlayer(), getPlayersWithWinPercentage() i playersWhoPlayed().
     */
    public PlayerMongoDTO obtainWinPercentage(PlayerMongo player) {
        PlayerMongo playerValidated = ifExistsPlayer(player);
        PlayerMongoDTO playerDTOToReturn = convertPlayerToDTO(playerValidated);

        if (playerValidated.getGamesHistory().isEmpty()) {
            playerDTOToReturn.setWinPercentage("Cannot calculate a player's winning percentage with no games played");
        } else {
            long totalGames = playerValidated.getGamesHistory().size();
            long totalWins = playerValidated.getGamesHistory().stream().filter(g -> g.getResult().equals("WINNER")).count();
            double winPercentage = ((double) totalWins / totalGames) * 100.0d;

            playerDTOToReturn.setWinPercentage(String.format("%.2f", winPercentage));
        }
        return playerDTOToReturn;
    }

    /**
     * Mètode per validar l'existència del Player a la base de dades.
     * S'utilitza en el mètode obtainWinPercentage().
     */
    public PlayerMongo ifExistsPlayer(PlayerMongo player) {
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
    public List<GameMongo> ifExistsGames(PlayerMongo playerById) {

        if (playerById.getGamesHistory().isEmpty()) {
            throw new GamesNotFoundException("There are no games played by " + playerById.getName());
        } else {
            return playerById.getGamesHistory();
        }
    }

    /**
     * Mètode per obtenir només els jugadors que han jugat, amb el percentatge d'èxit.
     * S'utilitza en els mètodes getWinningAverage(), getMostLoser() i getMostWinner().
     */
    public List<PlayerMongoDTO> playersWhoPlayed() {
        List<PlayerMongo> allPlayers = getListPlayers();
        boolean allPlayersWithEmptyGames = allPlayers.stream().allMatch(player -> player.getGamesHistory().isEmpty());

        List<PlayerMongoDTO> allPlayersDTO = new ArrayList<>();

        if (allPlayersWithEmptyGames) {
            throw new GamesNotFoundException("There are no games stored in the database");
        } else {
            allPlayers.forEach(p -> allPlayersDTO.add(obtainWinPercentage(p)));
            return allPlayersDTO.stream().filter(p -> !p.getWinPercentage().equals("Cannot calculate a player's winning percentage with no games played")).collect(Collectors.toList());
        }
    }

}