package br.com.poofinal.bands_api.service.login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.poofinal.bands_api.client.login.LoginClient;
import br.com.poofinal.bands_api.client.login.LoginRequest;

@Service
public class LoginService {

    @Autowired
    private LoginClient loginClient;

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String clientSecret;

    public String loginSpotify() {
        try {
            var request = new LoginRequest("client_credentials", clientId, clientSecret);
            String requestBody = "grant_type=" + request.grantType() +
                    "&client_id=" + request.clientId() +
                    "&client_secret=" + request.clientSecret();
            return loginClient.login(requestBody).accessToken();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to authenticate with Spotify");
        }
    }
}