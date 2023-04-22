package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    @Schema(description = "Identifier of the role", example = "1")
    private long id;

    @Column(name = "name", nullable = false)
    @Schema(description = "Name of the role", example = "USER")
    private String name;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, orphanRemoval = true, targetEntity = Player.class, cascade = CascadeType.ALL)
    @JsonIgnore
    @Schema(description = "List of players having the same role")
    private List<Player> players = new ArrayList<>();

    public Role(String name) {
        this.name = name;
    }

}
