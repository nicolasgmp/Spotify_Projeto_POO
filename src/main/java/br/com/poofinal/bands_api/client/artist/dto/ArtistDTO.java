package br.com.poofinal.bands_api.client.artist.dto;

import java.util.List;

public record ArtistDTO(String name, int followers, List<String> genres, String imgUrl, String artistUrl) {
    
}
