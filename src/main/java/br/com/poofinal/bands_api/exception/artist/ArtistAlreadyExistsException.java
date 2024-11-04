package br.com.poofinal.bands_api.exception.artist;

public class ArtistAlreadyExistsException extends RuntimeException{

    public ArtistAlreadyExistsException(String msg) {
        super(msg);
    }
    
}
