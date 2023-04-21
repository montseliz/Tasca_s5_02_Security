package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service.mysql;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mysql.IPlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IPlayerRepository playerRepository;

    @Autowired
    public CustomUserDetailsService (IPlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws PlayerNotFoundException {
        Player player = playerRepository.findByEmail(email).orElseThrow(() -> new PlayerNotFoundException("Player's email not found or incorrect"));

        Role role = player.getRole();

        return new User(player.getEmail(), player.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(role.getName())));
    }

}
