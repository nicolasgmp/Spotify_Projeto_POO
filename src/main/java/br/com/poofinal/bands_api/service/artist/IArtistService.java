package br.com.poofinal.bands_api.service.artist;

import java.util.List;

import org.springframework.security.core.Authentication;

import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.models.Artist;

public interface IArtistService {
    ArtistDTO createArtist(String artistName, Authentication auth);

    AlbumDTO saveArtistAlbum(String artistName, AlbumSpotify album, Authentication auth);

    List<ArtistDTO> findAllArtists(Authentication auth);

    List<ArtistDTO> findUserArtists(Authentication auth);

    Artist findArtistByName(String name);

}
