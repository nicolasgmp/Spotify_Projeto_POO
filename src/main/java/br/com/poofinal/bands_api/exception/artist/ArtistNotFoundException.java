package br.com.poofinal.bands_api.exception.artist;

public class ArtistNotFoundException extends RuntimeException{

    public ArtistNotFoundException(String msg) {
        super(msg);
    }
    
}
