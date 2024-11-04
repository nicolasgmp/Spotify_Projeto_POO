package br.com.poofinal.bands_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.poofinal.bands_api.models.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer>{
    
    Optional<Artist> findArtistByName(String name);
}
