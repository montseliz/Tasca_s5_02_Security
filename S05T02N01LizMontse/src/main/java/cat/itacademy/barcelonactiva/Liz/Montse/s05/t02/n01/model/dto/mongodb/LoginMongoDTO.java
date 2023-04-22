package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMongoDTO {

    @Schema(description = "Email of the player to log in", example = "montse@gmail.com")
    private String email;

    @Schema(description = "Password of the player to log in", example = "montse@gmail.com")
    private String password;

}
