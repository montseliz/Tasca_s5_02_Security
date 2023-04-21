package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {

    private String email;
    private String password;
    private String tokenType;
    private String accessToken;

    public AuthenticationResponseDTO(String email, String password, String accessToken) {
        this.email = email;
        this.password = password;
        this.tokenType = "Bearer";
        this.accessToken = accessToken;
    }
}
