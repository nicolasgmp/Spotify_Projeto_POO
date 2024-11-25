package br.com.poofinal.bands_api.service.artist;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.poofinal.bands_api.client.artist.ArtistClient;
import br.com.poofinal.bands_api.client.artist.dto.AlbumDTO;
import br.com.poofinal.bands_api.client.artist.dto.AlbumSpotify;
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
import br.com.poofinal.bands_api.models.User;
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
    public ArtistDTO createArtist(String artistName, Authentication auth) {
        long start = System.currentTimeMillis();
        var user = findAuthenticatedUser(auth);

        String token = loginService.loginSpotify();
        ArtistSpotify artistSpotify = findArtistFromSpotify(artistName, token);
        var existingArtist = artistRepository.findArtistByNameIgnoreCase(artistSpotify.name());

        if (existingArtist.isPresent()) {
            addArtistToUserFavorites(user, existingArtist.get());
            return ArtistMapper.fromArtistToDTO(existingArtist.get());
        }

        Artist newArtist = saveNewArtist(artistSpotify);
        saveArtistAlbums(newArtist, token);

        addArtistToUserFavorites(user, newArtist);
        long end = System.currentTimeMillis();
        System.out.println("Tempo de execução: " + (end - start) + "ms");

        return ArtistMapper.fromArtistToDTO(newArtist);
    }

    @Transactional
    public AlbumDTO saveArtistAlbum(String artistName, AlbumSpotify albumSpotify, Authentication auth) {
        Artist artist = this.findArtistByName(artistName);

        Album album = createOrUpdateAlbum(artist, albumSpotify);
        addAlbumToArtist(artist, album);

        return AlbumMapper.fromAlbumToDTO(album);
    }

    public List<ArtistDTO> findAllArtists(Authentication auth) {
        validateAdminAccess(auth);

        return artistRepository.findAll().stream()
                .peek(artist -> artist.setAlbums(findAlbumsSortedByPopularity(artist)))
                .map(ArtistMapper::fromArtistToDTO)
                .collect(Collectors.toList());
    }

    public List<ArtistDTO> findUserArtists(Authentication auth) {
        var user = findAuthenticatedUser(auth);

        if (user.getArtists().isEmpty()) {
            throw new ArtistNotFoundException("Você ainda não favoritou artistas");
        }

        return user.getArtists().stream()
                .peek(artist -> artist.setAlbums(findAlbumsSortedByPopularity(artist)))
                .map(ArtistMapper::fromArtistToDTO)
                .collect(Collectors.toList());
    }

    public Artist findArtistByName(String name) {
        return artistRepository.findArtistByNameIgnoreCase(name)
                .orElseThrow(() -> new ArtistNotFoundException("Artista não encontrado: " + name));
    }

    private void saveArtistAlbums(Artist artist, String token) {
        List<String> albumIds = findAlbumIdsForArtist(artist.getId(), token);
        List<AlbumSpotify> albumDetails = getAlbumDetailsWithPopularity(albumIds, token);

        albumDetails.forEach(albumSpotify -> {
            Album album = createOrUpdateAlbum(artist, albumSpotify);
            addAlbumToArtist(artist, album);
        });
    }

    private Artist saveNewArtist(ArtistSpotify artistSpotify) {
        Artist artist = ArtistMapper.fromSpotifyToArtist(artistSpotify);
        return artistRepository.save(artist);
    }

    private void addArtistToUserFavorites(User user, Artist artist) {
        user.getArtists().add(artist);
        userRepository.save(user);
    }

    private Album createOrUpdateAlbum(Artist artist, AlbumSpotify albumSpotify) {
        String normalizedName = normalizeAlbumName(albumSpotify.name());
        return albumRepository.findByNameAndArtist(normalizedName, artist)
                .orElseGet(() -> {
                    Album album = AlbumMapper.fromSpotifyToAlbum(albumSpotify, artist);
                    album.setName(normalizedName);
                    albumRepository.save(album);
                    return album;
                });
    }

    private void addAlbumToArtist(Artist artist, Album album) {
        artist.getAlbums().add(album);
        artistRepository.save(artist);
    }

    private List<AlbumSpotify> getAlbumDetailsWithPopularity(List<String> albumIds, String token) {
        List<AlbumSpotify> albums = new ArrayList<>();
        int size = 20;

        for (int i = 0; i < albumIds.size(); i += size) {
            List<String> batch = albumIds.subList(i, Math.min(i + size, albumIds.size()));
            String ids = String.join(",", batch);

            albums.addAll(artistClient.getAlbumsByIds("Bearer " + token, ids).albums());
        }

        return albums;
    }

    private List<String> findAlbumIdsForArtist(String spotifyId, String token) {
        return artistClient.getArtistAlbum("Bearer " + token, 50, 0, spotifyId)
                .items().stream()
                .map(AlbumSpotify::id)
                .collect(Collectors.toList());
    }

    private ArtistSpotify findArtistFromSpotify(String artistName, String token) {
        SearchArtistName res = artistClient.getArtistByName("Bearer " + token, "artist:" + artistName, "artist");

        if (res.artists().items().isEmpty()) {
            throw new ArtistNotFoundException("Artista não encontrado");
        }

        return res.artists().items().get(0);
    }

    private List<Album> findAlbumsSortedByPopularity(Artist artist) {
        return albumRepository.findByArtistOrderByPopularityDesc(artist);
    }

    private void validateAdminAccess(Authentication auth) {
        var user = findAuthenticatedUser(auth);

        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Acesso negado");
        }
    }

    private User findAuthenticatedUser(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }

    private String normalizeAlbumName(String albumName) {
        return albumName.replaceAll("\\s*(\\(.*?\\))$", "").trim();
    }
}
