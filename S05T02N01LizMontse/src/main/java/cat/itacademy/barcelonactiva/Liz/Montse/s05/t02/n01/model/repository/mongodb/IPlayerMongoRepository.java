package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mongodb;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.PlayerMongo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPlayerMongoRepository extends MongoRepository<PlayerMongo, ObjectId> {

    boolean existsByName(String name);

    boolean existsById(ObjectId id);

    Optional<PlayerMongo> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<PlayerMongo> findByRole(String roleName);

}
