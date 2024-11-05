package br.com.poofinal.bands_api.client.artist.dto;

import br.com.poofinal.bands_api.models.Artist;

public record AlbumDTO(String name, String releaseDate, String imgUrl,
                String spotifyUrl, Artist artist) {

}

