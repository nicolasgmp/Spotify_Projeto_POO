package br.com.poofinal.bands_api.service.artist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.poofinal.bands_api.client.artist.ArtistClient;
import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistAlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.client.artist.dto.ArtistSpotify;
import br.com.poofinal.bands_api.exception.artist.ArtistAlreadyExistsException;
import br.com.poofinal.bands_api.exception.artist.ArtistNotFoundException;
import br.com.poofinal.bands_api.models.Album;
import br.com.poofinal.bands_api.models.Artist;
import br.com.poofinal.bands_api.repository.AlbumRepository;
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

    @Autowired
    private AlbumRepository albumRepository;

    @Transactional
    public ArtistDTO createArtist(String id) {
        String token = loginService.loginSpotify();
        ArtistSpotify artistSpotify = artistClient.getArtistById("Bearer " + token, id);
        List<AlbumSpotify> allAlbums = new ArrayList<>();

        int limit = 10;
        int offset = 0;
        while(true) {
            ArtistAlbumSpotify artistAlbumSpotify = artistClient.getArtistAlbum("Bearer " + token, limit, offset, id);
            allAlbums.addAll(artistAlbumSpotify.items());

            if(artistAlbumSpotify.items().size() < limit) break;

            offset++;
        }
        var artistDB = artistRepository.findArtistByNameIgnoreCase(artistSpotify.name());

        if (artistDB.isPresent()) {
            throw new ArtistAlreadyExistsException("Artista já existente");
        }

        Artist newArtist = new Artist(artistSpotify.id(), artistSpotify.name(), artistSpotify.followers().total(),
                artistSpotify.externalUrls().spotify(), artistSpotify.images().get(0).url(), artistSpotify.genres(),
                List.of());
        artistRepository.save(newArtist);

        for (AlbumSpotify albumSpotify : allAlbums) {
            this.saveArtistAlbums(id, albumSpotify);
        }

        return new ArtistDTO(newArtist.getName(), newArtist.getFollowers(), newArtist.getGenres(),
                newArtist.getUrlImg(), newArtist.getUrlSpotify(), newArtist.getAlbums());
    }

    @Transactional
    public AlbumDTO saveArtistAlbums(String id, AlbumSpotify album) {
        Artist artist = this.findById(id);
        if (artist == null) {
            this.createArtist(id);
        }

        var newAlbum = new Album(album.id(), album.name(), album.releaseDate(), album.images().get(0).url(),
                album.externalUrls().spotify(), artist);
        albumRepository.save(newAlbum);
        this.addNewAlbum(id, newAlbum);

        return new AlbumDTO(newAlbum.getName(), newAlbum.getReleaseDate().toString(), newAlbum.getImgUrl(),
                newAlbum.getSpotifyUrl(), newAlbum.getArtist());
    }

    @Transactional
    public ArtistDTO addNewAlbum(String id, Album album) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException("Artista não encontrado"));
        artist.getAlbums().add(album);
        artistRepository.save(artist);

        artist.getAlbums().sort(Comparator.comparing(Album::getReleaseDate));

        return new ArtistDTO(artist.getName(), artist.getFollowers(), artist.getGenres(), artist.getUrlImg(),
                artist.getUrlSpotify(), artist.getAlbums());
    }

    public ArtistDTO findArtistByName(String name) {
        var artist = artistRepository.findArtistByNameIgnoreCase(name);
        if  (artist.isEmpty()) {
            throw new ArtistNotFoundException("Artista não cadastrados no DB");
        }

        var db = artist.get();
        List<Album> sortedAlbums = this.sortAlbumsByReleaseDate(db);
        var dto = new ArtistDTO(db.getName(), db.getFollowers(), db.getGenres(), db.getUrlImg(), db.getUrlSpotify(),
                sortedAlbums);

        return dto;
    }

    public List<ArtistDTO> findAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        if (artists.isEmpty()) {
            throw new ArtistNotFoundException("Não existem artistas cadastrados no DB");
        }

        var artistsDTO = artists.stream()
                .map(a -> {
                    this.sortAlbumsByReleaseDate(a);
                    return new ArtistDTO(a.getName(), a.getFollowers(),
                            a.getGenres(), a.getUrlImg(), a.getUrlSpotify(),
                            a.getAlbums());
                })
                .collect(Collectors.toList());

        return artistsDTO;
    }

    public Artist findById(String id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException("Artista não encontrado"));
    }

    private List<Album> sortAlbumsByReleaseDate(Artist artist) {
        List<Album> albums = artist.getAlbums();
        albums.sort((a1, a2) -> {
            LocalDate date1 = this.parseReleaseDate(a1.getReleaseDate());
            LocalDate date2 = this.parseReleaseDate(a2.getReleaseDate());
            return date1.compareTo(date2);
        });
        return albums;
    }

    private LocalDate parseReleaseDate(String releaseDateStr) {
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (releaseDateStr.length() == 4) {
            return LocalDate.of(Integer.parseInt(releaseDateStr), 1, 1);
        } else {
            return LocalDate.parse(releaseDateStr, formatter2);
        }
    }

}
