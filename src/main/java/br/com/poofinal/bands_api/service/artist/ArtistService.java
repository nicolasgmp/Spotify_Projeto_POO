package br.com.poofinal.bands_api.service.artist;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
import br.com.poofinal.bands_api.exception.user.AccessDeniedException;
import br.com.poofinal.bands_api.exception.user.UserNotFoundException;
import br.com.poofinal.bands_api.mapper.AlbumMapper;
import br.com.poofinal.bands_api.mapper.ArtistMapper;
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

    @Caching(evict = {
            @CacheEvict(value = "artists", allEntries = true),
            @CacheEvict(value = "user_artists", key = "#auth.name")
    })
    @Transactional

    public ArtistDTO createArtist(String artistName, Authentication auth) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
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
            return ArtistMapper.fromArtistToDTO(artistDB.get());
        }

        Artist newArtist = ArtistMapper.fromSpotifyToArtist(artistSpotify);
        artistRepository.save(newArtist);

        for (AlbumSpotify albumSpotify : albumsSpotify.items()) {
            this.saveArtistAlbum(newArtist.getName(), albumSpotify, auth);
        }

        user.getArtists().add(newArtist);
        userRepository.save(user);

        return ArtistMapper.fromArtistToDTO(newArtist);
    }

    @Transactional
    public AlbumDTO saveArtistAlbum(String name, AlbumSpotify album, Authentication auth) {
        Artist artist = artistRepository.findArtistByNameIgnoreCase(name)
                .orElseThrow(() -> new ArtistNotFoundException("Não foi encontrado um artista com este nome"));
        Album newAlbum = AlbumMapper.fromSpotifyToAlbum(album, artist);
        albumRepository.save(newAlbum);
        this.addNewAlbum(name, newAlbum);

        return AlbumMapper.fromAlbumToDTO(newAlbum);
    }

    @Transactional
    public ArtistDTO addNewAlbum(String name, Album album) {
        Artist artist = artistRepository.findArtistByNameIgnoreCase(name)
                .orElseThrow(() -> new ArtistNotFoundException("Não foi encontrado um artista com este nome"));
        artist.getAlbums().add(album);
        artistRepository.save(artist);

        artist.getAlbums().sort(Comparator.comparing(Album::getReleaseDate));

        return ArtistMapper.fromArtistToDTO(artist);
    }

    @Cacheable("artists")
    public List<ArtistDTO> findAllArtists(Authentication auth) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }

        List<Artist> artists = artistRepository.findAll();
        if (artists.isEmpty()) {
            throw new ArtistNotFoundException("Não existem artistas cadastrados no DB");
        }

        var artistsDTO = artists.stream()
                .map(a -> {
                    this.sortAlbumsByReleaseDate(a);
                    return ArtistMapper.fromArtistToDTO(a);
                })
                .collect(Collectors.toList());
        return artistsDTO;
    }

    @Cacheable("user_artists")
    public List<ArtistDTO> findUserArtists(Authentication auth) {
        var user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        if (user.getArtists().isEmpty()) {
            throw new ArtistNotFoundException("Você ainda não favoritou artistas");
        }

        var artistsDTO = user.getArtists().stream()
                .map(a -> {
                    this.sortAlbumsByReleaseDate(a);
                    return ArtistMapper.fromArtistToDTO(a);
                })
                .collect(Collectors.toList());
        return artistsDTO;
    }

    public ArtistDTO findArtistByName(String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        var artistDB = artistRepository.findArtistByNameIgnoreCase(name);
        if (artistDB.isPresent()) {
            return ArtistMapper.fromArtistToDTO(artistDB.get());
        }

        String token = loginService.loginSpotify();
        SearchArtistName res = artistClient.getArtistByName("Bearer " + token, name, "artist");
        ArtistSpotify artist = res.artists().items().get(0);
        List<AlbumSpotify> resAlbums = artistClient.getArtistAlbum("Bearer " + token, 50, 0, artist.id()).items();

        return ArtistMapper.fromSpotifyToDTO(artist, resAlbums);
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
