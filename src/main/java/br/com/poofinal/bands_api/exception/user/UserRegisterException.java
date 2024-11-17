package br.com.poofinal.bands_api.exception.user;

public class UserRegisterException extends RuntimeException {
    public UserRegisterException(String msg) {
        super(msg);
    }
}