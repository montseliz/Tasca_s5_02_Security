package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoleMongo {

    @Field(name = "name")
    @Schema(description = "Name of the role", example = "USER")
    private String name;

}
