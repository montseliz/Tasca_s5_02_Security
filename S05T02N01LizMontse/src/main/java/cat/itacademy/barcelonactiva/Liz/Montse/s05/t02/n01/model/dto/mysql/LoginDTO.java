package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @Schema(description = "Email of the player to log in", example = "montse@gmail.com")
    private String email;

    @Schema(description = "Password of the player to log in", example = "montse@gmail.com")
    private String password;

}
