package br.com.poofinal.bands_api.mapper;

import java.util.ArrayList;
import java.util.List;

import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.client.artist.dto.ArtistSpotify;
import br.com.poofinal.bands_api.models.Album;
import br.com.poofinal.bands_api.models.Artist;

public class ArtistMapper {

    public static ArtistDTO fromArtistToDTO(Artist a) {
        return new ArtistDTO(a.getName(), a.getFollowers(), a.getGenres(), a.getUrlImg(), a.getUrlImg(), a.getAlbums());
    }

    public static ArtistDTO fromSpotifyToDTO(ArtistSpotify a, List<AlbumSpotify> as) {
        List<Album> albums = as.stream()
                .map(albumSpotify -> AlbumMapper.fromSpotifyToAlbum(albumSpotify, new Artist()))
                .toList();
        return new ArtistDTO(a.name(), a.followers().total(), a.genres(),
                a.images().get(0).url(), a.externalUrls().spotify(), albums);
    }

    public static Artist fromDTOToArtist(ArtistDTO a, String id) {
        return new Artist(id, a.name(), a.followers(), a.artistUrl(), a.imgUrl(), a.genres(), a.albums());
    }

    public static Artist fromSpotifyToArtist(ArtistSpotify a) {
        Artist artist = new Artist(a.id(), a.name(), a.followers().total(), a.externalUrls().spotify(),
                a.images().get(0).url(), a.genres(), new ArrayList<>());
        return artist;
    }

}
