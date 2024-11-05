package br.com.poofinal.bands_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.poofinal.bands_api.models.Album;

public interface AlbumRepository extends JpaRepository<Album, String>{
    
}
