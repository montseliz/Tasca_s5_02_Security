package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.dto.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPlayerDTO {

    private long id;
    private String name;
    private LocalDateTime registration;
    private String email;
    private String password;
    private Role role;

}
