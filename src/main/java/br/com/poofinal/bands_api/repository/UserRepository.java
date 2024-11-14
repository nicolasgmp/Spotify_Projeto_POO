package br.com.poofinal.bands_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.poofinal.bands_api.models.User;

public interface UserRepository extends JpaRepository<User, String>{
    Optional<User> findByUsername(String username);
}
