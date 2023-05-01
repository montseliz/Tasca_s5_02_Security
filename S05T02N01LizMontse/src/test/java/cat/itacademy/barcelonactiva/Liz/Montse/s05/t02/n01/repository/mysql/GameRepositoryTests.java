package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.repository.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Game;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IGameRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IPlayerRepository;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IRoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GameRepositoryTests {

    @Autowired
    private IGameRepository gameRepository;
    @Autowired
    private IPlayerRepository playerRepository;
    @Autowired
    private IRoleRepository roleRepository;

    private Game game;
    private Player player;

    /**
     * Arrange -> condició prèvia o configuració
     */
    @BeforeEach
    void setup() {
        Role role = new Role("USER");
        roleRepository.save(role);

        player = Player.builder()
                .name("Montse")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("montse@gmail.com")
                .password("password123")
                .role(role).build();
        playerRepository.save(player);

        game = Game.builder()
                .dice1((short) 1)
                .dice2((short) 6)
                .result(Game.ResultGame.WINNER)
                .player(player).build();
    }

    @DisplayName("Test to save a game in the database")
    @Test
    void testSaveAGame() {

        /**
         * Act -> acció o comportament que testegem
         */
        Game savedGame = gameRepository.save(game);

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(savedGame).isNotNull();
        Assertions.assertThat(savedGame.getId()).isPositive();
    }

    @DisplayName("Test to check if game repository is empty")
    @Test
    void testGameRepositoryIsEmpty() {

        /**
         * Act -> acció o comportament que testegem
         */
        gameRepository.save(game);
        gameRepository.deleteById(player.getId());
        Optional<Game> gameOptional = gameRepository.findById(player.getId());

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(gameOptional).isEmpty();
    }

}
