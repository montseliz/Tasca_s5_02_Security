package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.repository.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IRoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTests {

    @Autowired
    private IRoleRepository roleRepository;

    private Role role;

    /**
     * Arrange -> condició prèvia o configuració
     */
    @BeforeEach
    void setup() {

        role = Role.builder()
                .name("USER")
                .players(new ArrayList<>()).build();
    }

    @DisplayName("Test to save a role in the database")
    @Test
    void testSaveARole() {

        /**
         * Act -> acció o comportament que testegem
         */
        Role savedRole = roleRepository.save(role);

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(savedRole).isNotNull();
        Assertions.assertThat(savedRole.getId()).isPositive();
    }

    @DisplayName("Test to find a role by name in the database")
    @Test
    void testFindRoleByName() {

        /**
         * Act -> acció o comportament que testegem
         */
        roleRepository.save(role);

        Role roleFromDB = roleRepository.findByName(role.getName()).get();

        /**
         * Assert -> verificar la sortida
         */
        Assertions.assertThat(roleFromDB).isNotNull();
    }

}
