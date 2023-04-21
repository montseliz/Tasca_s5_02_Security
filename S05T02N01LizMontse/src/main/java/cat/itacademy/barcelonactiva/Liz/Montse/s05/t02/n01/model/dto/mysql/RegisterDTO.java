package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {

    private String name;
    private String email;
    private String password;

}
