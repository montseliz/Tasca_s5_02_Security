package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPlayerRepository extends JpaRepository<Player, Long> {

    boolean existsByName(String name);

    boolean existsById(long id);

    Optional<Player> findByEmail(String email);

    boolean existsByEmail(String email);

}
