package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "players")
public class PlayerMongo {

    @Id
    @Schema(description = "Identifier of the player", example = "643d909f15da8348ee4805c1")
    private ObjectId id;

    @Field(name = "name")
    @Schema(description = "Username of the player", example = "Montse")
    private String name;

    @CreationTimestamp
    @Field(name = "registration_date")
    @Schema(description = "Registration date of the player", example = "2023-04-10 18:46:38.227499")
    private LocalDateTime registration;

    @Field(name = "games")
    @Schema(description = "Player's games history")
    private List<GameMongo> gamesHistory = new ArrayList<>();

    @Email
    @NotBlank
    @Field(name = "email")
    @Schema(description = "Email of the player", example = "montse@gmail.com")
    private String email;

    @NotBlank
    @Field(name = "password")
    @Schema(description = "Password of the player", example = "password123")
    private String password;

    @Field(name = "role")
    @Schema(description = "Role of the player", example = "USER")
    private RoleMongo role;

    public PlayerMongo(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.registration = LocalDateTime.now();
    }

    public void addGamesHistory(GameMongo game) {
        gamesHistory.add(game);
    }

}

