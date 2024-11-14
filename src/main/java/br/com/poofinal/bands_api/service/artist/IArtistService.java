package br.com.poofinal.bands_api.service.artist;

import java.util.List;

import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.models.Album;
import br.com.poofinal.bands_api.models.Artist;

public interface IArtistService {
    ArtistDTO createArtist(String id);
    AlbumDTO saveArtistAlbum(String id, AlbumSpotify album);
    ArtistDTO addNewAlbum(String id, Album album);
    ArtistDTO findArtistByName(String name);
    List<ArtistDTO> findAllArtists();
    Artist findById(String id);
}
