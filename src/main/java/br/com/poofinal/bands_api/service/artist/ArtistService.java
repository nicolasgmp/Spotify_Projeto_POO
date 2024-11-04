package br.com.poofinal.bands_api.service.artist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.poofinal.bands_api.client.artist.ArtistClient;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.client.artist.dto.ArtistSpotify;
import br.com.poofinal.bands_api.exception.artist.ArtistAlreadyExistsException;
import br.com.poofinal.bands_api.exception.artist.ArtistNotFoundException;
import br.com.poofinal.bands_api.models.Artist;
import br.com.poofinal.bands_api.repository.ArtistRepository;
import br.com.poofinal.bands_api.service.login.LoginService;

@Service
public class ArtistService {

    @Autowired
    private ArtistClient artistClient;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ArtistRepository artistRepository;

    public ArtistDTO findArtistSpotify(String id) {
        try {
            String token = loginService.loginSpotify();
            ArtistSpotify res = artistClient.getArtistById("Bearer " + token, id);
            var artistDB = artistRepository.findArtistByName(res.name());
            if (artistDB.isEmpty()) {
                var artistDTO = this.createArtist(res);
                return artistDTO;
            }
            String imageURL = res.images().get(0).url();
            var artistDto = new ArtistDTO(res.name(), res.followers().total(), res.genres(), imageURL,
                    res.externalUrls().spotify());
            return artistDto;
        } catch (Exception e) {
            throw new ArtistNotFoundException("Artista não encontrado no spotify");
        }
    }

    public ArtistDTO createArtist(ArtistSpotify artistSpotify) {
        var artistDB = artistRepository.findArtistByName(artistSpotify.name());
        if (artistDB.isPresent()) {
            throw new ArtistAlreadyExistsException("Artista já existente");
        }

        Artist newArtist = new Artist(null, artistSpotify.name(), artistSpotify.followers().total(),
                artistSpotify.externalUrls().spotify(), artistSpotify.images().get(0).url(), artistSpotify.genres());
        artistRepository.save(newArtist);

        return new ArtistDTO(newArtist.getName(), newArtist.getFollowers(), newArtist.getGenres(),
                newArtist.getUrlSpotify(), newArtist.getUrlImg());
    }

    public ArtistDTO createArtist(String id) {
        String token = loginService.loginSpotify();
        ArtistSpotify res = artistClient.getArtistById("Bearer " + token, id);
        var artistDB = artistRepository.findArtistByName(res.name());
        if (artistDB.isPresent()) {
            throw new ArtistAlreadyExistsException("Artista já existente");
        }
        Artist newArtist = new Artist(null, res.name(), res.followers().total(), res.externalUrls().spotify(),
                res.images().get(0).url(), res.genres());
        artistRepository.save(newArtist);
        return new ArtistDTO(newArtist.getName(), newArtist.getFollowers(), newArtist.getGenres(),
                newArtist.getUrlImg(), newArtist.getUrlSpotify());
    }

    public List<ArtistDTO> findAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        if (artists.isEmpty()) {
            throw new ArtistNotFoundException("Não existem artistas cadastrados no DB");
        }
        var artistsDTO = artists.stream()
                .map(a -> new ArtistDTO(a.getName(), a.getFollowers(),
                        a.getGenres(), a.getUrlImg(), a.getUrlSpotify()))
                .collect(Collectors.toList());
        return artistsDTO;
    }
}
