package br.com.poofinal.bands_api.client.artist.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AlbumSpotify(String id, String name, String releaseDate, List<AlbumImage> images, ExternalUrl externalUrls) {

    public record AlbumImage(String url, int height, int width) {
    }

    public record ExternalUrl(String spotify) {
    }
}
