package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mongodb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseMongoDTO {

    @Schema(description = "Email of the player logged in", example = "montse@gmail.com")
    private String email;

    @Schema(description = "Password of the player logged in", example = "password123")
    private String password;

    @Schema(description = "Type of the token", example = "Bearer")
    private String tokenType;

    @Schema(description = "Token of the player logged in", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb3NlbGl0b0BnbWFpbC5jb20iLCJpYXQiOjE2ODIxMDYzOTIsImV4cCI6MTY4MjEwNzA5Mn0._9hWbUj-S4b2auQN4boInp4ky8GnCdcCvv1YOqDkS4s")
    private String accessToken;

    public AuthenticationResponseMongoDTO(String email, String password, String accessToken) {
        this.email = email;
        this.password = password;
        this.tokenType = "Bearer";
        this.accessToken = accessToken;
    }
}