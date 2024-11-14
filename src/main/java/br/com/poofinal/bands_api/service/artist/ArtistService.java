package br.com.poofinal.bands_api.service.artist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.poofinal.bands_api.client.artist.ArtistClient;
import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistAlbumSpotify;
import br.com.poofinal.bands_api.client.artist.dto.ArtistDTO;
import br.com.poofinal.bands_api.client.artist.dto.ArtistSpotify;
import br.com.poofinal.bands_api.client.artist.dto.SearchArtistName;
import br.com.poofinal.bands_api.exception.artist.ArtistNotFoundException;
import br.com.poofinal.bands_api.models.Album;
import br.com.poofinal.bands_api.models.Artist;
import br.com.poofinal.bands_api.models.enums.UserRole;
import br.com.poofinal.bands_api.repository.AlbumRepository;
import br.com.poofinal.bands_api.repository.ArtistRepository;
import br.com.poofinal.bands_api.repository.UserRepository;
import br.com.poofinal.bands_api.service.login.LoginService;

@Service
public class ArtistService implements IArtistService {

    @Autowired
    private ArtistClient artistClient;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ArtistDTO createArtist(String artistName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userRepository.findByUsername(auth.getName()).get();
        String token = loginService.loginSpotify();
        SearchArtistName res = artistClient.getArtistByName("Bearer " + token, "artist:" + artistName, "artist");

        if (res.artists().items().isEmpty()) {
            throw new ArtistNotFoundException("Artista não encontrado");
        }

        ArtistSpotify artistSpotify = res.artists().items().get(0);

        ArtistAlbumSpotify albumsSpotify = artistClient.getArtistAlbum("Bearer " + token, 50, 0, artistSpotify.id());

        var artistDB = artistRepository.findArtistByNameIgnoreCase(artistSpotify.name());

        if (artistDB.isPresent()) {
            user.getArtists().add(artistDB.get());
            userRepository.save(user);
            return new ArtistDTO(artistDB.get().getName(), artistDB.get().getFollowers(), artistDB.get().getGenres(),
                    artistDB.get().getUrlImg(), artistDB.get().getUrlSpotify(), artistDB.get().getAlbums());
        }

        Artist newArtist = new Artist(artistSpotify.id(), artistSpotify.name(), artistSpotify.followers().total(),
                artistSpotify.externalUrls().spotify(), artistSpotify.images().get(0).url(), artistSpotify.genres(),
                List.of());
        artistRepository.save(newArtist);

        for (AlbumSpotify albumSpotify : albumsSpotify.items()) {
            this.saveArtistAlbum(newArtist.getId(), albumSpotify);
        }

        user.getArtists().add(newArtist);
        userRepository.save(user);

        return new ArtistDTO(newArtist.getName(), newArtist.getFollowers(), newArtist.getGenres(),
                newArtist.getUrlImg(), newArtist.getUrlSpotify(), newArtist.getAlbums());
    }

    @Transactional
    public AlbumDTO saveArtistAlbum(String id, AlbumSpotify album) {
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
        if (artist.isEmpty()) {
            throw new ArtistNotFoundException("Artista não cadastrados no DB");
        }

        var db = artist.get();
        List<Album> sortedAlbums = this.sortAlbumsByReleaseDate(db);
        var dto = new ArtistDTO(db.getName(), db.getFollowers(), db.getGenres(), db.getUrlImg(), db.getUrlSpotify(),
                sortedAlbums);

        return dto;
    }

    public List<ArtistDTO> findAllArtists() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userRepository.findByUsername(auth.getName());
        if (user.get().getRole() != UserRole.ADMIN) {
            throw new ArtistNotFoundException("Acesso negado");
        }

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

    public List<ArtistDTO> findUserArtists() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var user = userRepository.findByUsername(auth.getName());

        if (user.get().getArtists().isEmpty()) {
            throw new ArtistNotFoundException("Você ainda não favoritou artistas");
        }

        var artistsDTO = user.get().getArtists().stream()
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
