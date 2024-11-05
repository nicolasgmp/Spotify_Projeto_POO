package br.com.poofinal.bands_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.poofinal.bands_api.models.Artist;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, String> {

    @Query("SELECT a FROM Artist a WHERE TRIM(LOWER(a.name)) = TRIM(LOWER(?1))")
    Optional<Artist> findArtistByNameIgnoreCase(String name);
}
