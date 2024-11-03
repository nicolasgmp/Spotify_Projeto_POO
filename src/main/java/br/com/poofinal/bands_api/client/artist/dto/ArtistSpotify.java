package br.com.poofinal.bands_api.client.artist.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ArtistSpotify(String name, Follower followers, List<String> genres, List<ArtistImages> images,
        ExternalUrl externalUrls) {

    public record Follower(int total) {
    }

    public record ArtistImages(String url, int height, int width) {
    }

    public record ExternalUrl(String spotify) {
    }
}
