package br.com.poofinal.bands_api.config;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.poofinal.bands_api.models.User;
import br.com.poofinal.bands_api.models.enums.UserRole;
import br.com.poofinal.bands_api.repository.UserRepository;

@Component
public class Startup implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            var admin = new User(UUID.randomUUID().toString(), "admin", "admin", encoder.encode("admin"),
                    UserRole.ADMIN,
                    List.of());

            userRepository.save(admin);
        }
    }

}
