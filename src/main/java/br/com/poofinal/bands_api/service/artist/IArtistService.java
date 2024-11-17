package br.com.poofinal.bands_api.service.artist;

import java.util.List;

import org.springframework.security.core.Authentication;

import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.models.Album;

public interface IArtistService {
    ArtistDTO createArtist(String id, Authentication auth);
    AlbumDTO saveArtistAlbum(String id, AlbumSpotify album, Authentication auth);
    ArtistDTO addNewAlbum(String id, Album album);
    List<ArtistDTO> findAllArtists(Authentication auth);
    List<ArtistDTO> findUserArtists(Authentication auth);
    ArtistDTO findArtistByName(String name);
}
