package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.repository.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PlayerRepositoryTests {

    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IPlayerRepository playerRepository;

    private Role role;
    private Player player;

    /**
     * Arrange -> condició prèvia o configuració
     */
    @BeforeEach
    void setup() {
        role = new Role("USER");
        roleRepository.save(role);

        player = Player.builder()
                .name("Montse")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("montse@gmail.com")
                .password("password123")
                .role(role).build();
    }

    @DisplayName("Test to save a player in the database")
    @Test
    void testSaveAPlayer() {

        /**
         * Act -> acció o comportament que testegem
         */
        Player savedPlayer = playerRepository.save(player);

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(savedPlayer).isNotNull();
        Assertions.assertThat(savedPlayer.getId()).isPositive();
    }

    @DisplayName("Test to get all players from the database")
    @Test
    void testGetAllPlayers() {

        /**
         * Arrange -> condició prèvia o configuració
         */
        Player player2 = Player.builder()
                .name("Roser")
                .registration(LocalDateTime.now())
                .gamesHistory(new ArrayList<>())
                .email("roser@gmail.com")
                .password("password456")
                .role(role).build();

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);
        playerRepository.save(player2);

        List<Player> playersSaved = playerRepository.findAll();

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(playersSaved).isNotEmpty().hasSize(2);
    }

    @DisplayName("Test to find a player by id in the database")
    @Test
    void testFindPlayerById() {

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);

        Player playerFromDB = playerRepository.findById(player.getId()).get();

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(playerFromDB).isNotNull();
    }

    @DisplayName("Test to find a player by email in the database")
    @Test
    void testFindPlayerByEmail() {

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);

        Player playerFromDB = playerRepository.findByEmail(player.getEmail()).get();

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(playerFromDB).isNotNull();
    }

    @DisplayName("Test to update a player by name in the database")
    @Test
    void testUpdatePlayerByName() {

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);

        Player playerFromDB = playerRepository.findById(player.getId()).get();
        playerFromDB.setName("Carles");

        Player playerUpdated = playerRepository.save(playerFromDB);

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(player.getName()).isEqualTo("Carles");
        assertEquals(playerFromDB, playerUpdated);
    }

    @DisplayName("Test to check if a player exists by name")
    @Test
    void testPlayerExistsByName() {

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);

        boolean exists = playerRepository.existsByName(player.getName());

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(exists).isTrue();
    }

    @DisplayName("Test to check if a player exists by id")
    @Test
    void testPlayerExistsById() {

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);

        boolean exists = playerRepository.existsById(player.getId());

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(exists).isTrue();
    }

    @DisplayName("Test to check if a player exists by email")
    @Test
    void testPlayerExistsByEmail() {

        /**
         * Act -> acció o comportament que testegem
         */
        playerRepository.save(player);

        boolean exists = playerRepository.existsByEmail(player.getEmail());

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(exists).isTrue();
    }

}
