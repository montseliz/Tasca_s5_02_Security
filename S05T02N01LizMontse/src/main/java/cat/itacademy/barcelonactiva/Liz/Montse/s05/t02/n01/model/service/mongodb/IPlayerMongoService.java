package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mongodb;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.PlayerMongo;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.PlayerMongoDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb.RegisterMongoDTO;
import org.bson.types.ObjectId;

import java.util.List;

public interface IPlayerMongoService {

    PlayerMongo getPlayerById(ObjectId id);
    List<PlayerMongo> getListPlayers();
    PlayerMongoDTO editPlayer(ObjectId id, RegisterMongoDTO registerMongoDTO);
    List<PlayerMongoDTO> getPlayersWithWinPercentage();
    String getWinningAverage();
    PlayerMongoDTO getMostLoser();
    PlayerMongoDTO getMostWinner();

}