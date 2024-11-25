package br.com.poofinal.bands_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.poofinal.bands_api.models.Album;
import br.com.poofinal.bands_api.models.Artist;

public interface AlbumRepository extends JpaRepository<Album, String> {
    Optional<Album> findByNameAndArtist(String name, Artist artist);

    List<Album> findByArtistOrderByPopularityDesc(Artist artist);
}
