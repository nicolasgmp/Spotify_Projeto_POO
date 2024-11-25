package br.com.poofinal.bands_api.client.artist;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.poofinal.bands_api.client.artist.dto.AlbumBatch;
import br.com.poofinal.bands_api.client.artist.dto.ArtistAlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistSpotify;
import br.com.poofinal.bands_api.client.artist.dto.SearchArtistName;

@FeignClient(name = "ArtistClient", url = "https://api.spotify.com/v1")
public interface ArtistClient {

        @GetMapping("/artists/{id}")
        ArtistSpotify getArtistById(
                        @RequestHeader("Authorization") String auth,
                        @PathVariable String id);

        @GetMapping("/artists/{id}/albums")
        ArtistAlbumSpotify getArtistAlbum(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam("limit") int limit,
                        @RequestParam("offset") int offset,
                        @PathVariable String id);

        @GetMapping("/search")
        SearchArtistName getArtistByName(
                        @RequestHeader("Authorization") String auth,
                        @RequestParam("query") String artistName,
                        @RequestParam("type") String type);

        @GetMapping("/albums")
        AlbumBatch getAlbumsByIds(@RequestHeader("Authorization") String auth, @RequestParam String ids);
}
