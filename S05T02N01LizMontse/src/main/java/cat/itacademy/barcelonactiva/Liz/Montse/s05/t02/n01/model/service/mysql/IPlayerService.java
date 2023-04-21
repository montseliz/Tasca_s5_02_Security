package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.PlayerDTO;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql.RegisterDTO;

import java.util.List;

public interface IPlayerService {

    Player getPlayerById(long id);
    List<Player> getListPlayers();
    PlayerDTO editPlayer(long id, RegisterDTO registerDTO);
    List<PlayerDTO> getPlayersWithWinPercentage();
    String getWinningAverage();
    PlayerDTO getMostLoser();
    PlayerDTO getMostWinner();

}
