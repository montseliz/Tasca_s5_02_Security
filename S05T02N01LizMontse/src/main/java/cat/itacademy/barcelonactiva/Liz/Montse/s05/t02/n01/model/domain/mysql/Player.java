package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Schema(description = "Identifier of the player", example = "1")
    private long id;

    @Column(name = "name", nullable = false)
    @Schema(description = "Username of the player", example = "Montse")
    private String name;

    /**
     * L'atribut s'inicialitza automàticament (amb l'hora i data locals) en l'instant en què es crea una nova instància de Player,
     * gràcies a l'anotació @CreationTimestamp.
     */
    @CreationTimestamp
    @Column(name = "registration_date", nullable = false)
    @Schema(description = "Registration date of the player", example = "2023-04-10 18:46:38.227499")
    private LocalDateTime registration;

    /**
     * La propietat "mappedBy" s'estableix en "player" per indicar que la relació @OneToMany està mapejada per l'atribut "player" de l'entitat Game.
     * En el cas de "fetch" en "EAGER" significa que els "games" es carregaran automàticament en la memòria quan el "player" es carregui.
     * Pel que fa a la propietat "orphanRemoval" en "true" vol dir que els "games" s'eliminaran en el moment en què s'elimini un "player".
     * La propietat "targetEntity" fa referència a la classe de l'entitat relacionada. Per tant, la relació @OneToMany apuntarà a la classe Game.
     * Finalment, la propietat "cascade" s'estableix en "ALL" per indicar que totes les operacions de cascada (creació, actualització i eliminació),
     * que s'apliquin a l'entitat Player, també s'aplicaran automàticament a l'entitat Game.
     */
    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = Game.class, cascade = CascadeType.ALL)
    @Schema(description = "Player's games history", example = "[Game{id=1, dice1=4, dice2=3, result=WINNER, player=1}, Game{id=2, dice1=5, dice2=1, result=LOSER, player=1}]")
    private List<Game> gamesHistory = new ArrayList<>();

    /**
     * L'anotació @Email s'utilitza per validar que el valor de la propietat és una adreça de correu electrònic vàlida.
     */
    @Column(name = "email", nullable = false, unique = true)
    @Email
    @Schema(description = "Email of the player", example = "montse@gmail.com")
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Schema(description = "Password of the player", example = "password123")
    private String password;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @Schema(description = "Role of the player", example = "USER")
    private Role role;

    public Player(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.registration = LocalDateTime.now();
    }

    public void addGamesHistory(Game game) {
        gamesHistory.add(game);
    }

}
