package br.com.poofinal.bands_api.service.artist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.poofinal.bands_api.client.artist.ArtistClient;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.client.artist.dto.ArtistSpotify;
import br.com.poofinal.bands_api.service.login.LoginService;

@Service
public class ArtistService {

    @Autowired
    private ArtistClient artistClient;

    @Autowired
    private LoginService loginService;

    public ArtistDTO findArtistSpotify(String id) {
        try {
            String token = loginService.loginSpotify();
            ArtistSpotify res = artistClient.getArtistById("Bearer " + token, id);
            String imageURL = res.images().get(0).url();
            var artist = new ArtistDTO(res.name(), res.followers().total(), res.genres(), imageURL, res.externalUrls().spotify());
            return artist;
        } catch (Exception e) {
            throw new RuntimeException("Artist not found in Spotify: " + e.getMessage());
        }
    }
}
