package br.com.poofinal.bands_api.client.artist.dto;

import java.util.List;

import br.com.poofinal.bands_api.models.Album;

public record ArtistDTO(String name, int followers, List<String> genres, String imgUrl, String artistUrl, List<Album> albums) {
    
}
