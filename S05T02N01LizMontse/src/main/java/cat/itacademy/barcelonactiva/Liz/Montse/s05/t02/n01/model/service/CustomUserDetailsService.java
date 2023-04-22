package cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.service;

import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mongodb.PlayerMongo;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Player;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.domain.mysql.Role;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.exception.PlayerNotFoundException;
import cat.itacademy.barcelonactiva.Liz.Montse.s05.t02.n01.model.repository.mongodb.IPlayerMongoRepository;
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
    private final IPlayerMongoRepository playerMongoRepository;

    @Autowired
    public CustomUserDetailsService (IPlayerRepository playerRepository, IPlayerMongoRepository playerMongoRepository) {
        this.playerRepository = playerRepository;
        this.playerMongoRepository = playerMongoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws PlayerNotFoundException {
        try {
            Player player = playerRepository.findByEmail(email).orElse(null);
            if (player != null) {
                Role role = player.getRole();
                return new User(player.getEmail(), player.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(role.getName())));
            } else {
                PlayerMongo playerMongo = playerMongoRepository.findByEmail(email).orElseThrow(() -> new PlayerNotFoundException("Player's email not found or incorrect"));
                String roleName = String.valueOf(playerMongo.getRole());
                return new User(playerMongo.getEmail(), playerMongo.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(roleName)));
            }
        } catch (Exception e) {
            throw new PlayerNotFoundException("Player's email not found or incorrect");
        }
    }
}

