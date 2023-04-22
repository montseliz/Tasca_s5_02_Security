package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPlayerDTO {

    @Schema(description = "Identifier of the player registered", example = "1")
    private long id;

    @Schema(description = "Name of the player registered", example = "montse")
    private String name;

    @Schema(description = "Registration date of the player", example = "2023-04-10 18:46:38.227499")
    private LocalDateTime registration;

    @Schema(description = "Email of the player registered", example = "montse@gmail.com")
    private String email;

    @Schema(description = "Password of the player registered", example = "password123")
    private String password;

    @Schema(description = "Role of the player registered", example = "USER")
    private Role role;

}
