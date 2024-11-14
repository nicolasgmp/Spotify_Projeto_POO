package br.com.poofinal.bands_api.client.artist.dto;

import java.util.List;

public record SearchArtistName(SearchedArtist artists) {

    public record SearchedArtist(List<ArtistSpotify> items) {
    }

}
