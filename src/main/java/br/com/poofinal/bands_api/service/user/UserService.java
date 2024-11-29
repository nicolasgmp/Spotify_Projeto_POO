package br.com.poofinal.bands_api.service.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.poofinal.bands_api.repository.UserRepository;
import br.com.poofinal.bands_api.service.artist.ArtistService;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistService artistService;

    public void removeFavorite(String username, String artistName) {
        var user = userRepository.findByUsername(username);
        var artist = artistService.findArtistByName(artistName);
        user.get().getArtists().remove(artist);
        userRepository.save(user.get());
    }

    public boolean PasswordMatchesPattern(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
        Matcher matcher = pattern.matcher(password);
        return matcher.find();
    }
}
