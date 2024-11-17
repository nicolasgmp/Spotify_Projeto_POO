package br.com.poofinal.bands_api.exception.user;

public class UserLoginException extends RuntimeException {
    public UserLoginException(String msg) {
        super(msg);
    }

}
