package br.com.poofinal.bands_api.mapper;

import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.models.Album;
import br.com.poofinal.bands_api.models.Artist;

public class AlbumMapper {

    public static Album fromSpotifyToAlbum(AlbumSpotify as, Artist a) {
        String img = as.images().get(0).url();
        String link = as.externalUrls().spotify();
        return new Album(as.id(), as.name(), as.releaseDate(), img, link, a);
    }

    public static AlbumDTO fromAlbumToDTO(Album a) {
        return new AlbumDTO(a.getName(), a.getReleaseDate(), a.getImgUrl(), a.getSpotifyUrl(), a.getArtist());
    }
}
