package br.com.poofinal.bands_api.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.poofinal.bands_api.exception.artist.ArtistNotFoundException;
import br.com.poofinal.bands_api.exception.user.AccessDeniedException;
import br.com.poofinal.bands_api.exception.user.UnauthorizedException;
import br.com.poofinal.bands_api.exception.user.UserLoginException;
import br.com.poofinal.bands_api.exception.user.UserNotFoundException;
import br.com.poofinal.bands_api.exception.user.UserRegisterException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ArtistNotFoundException.class)
    public String handleArtistNotFoundException(ArtistNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("redirectUrl", "/api/v1/artists/new");
        return "view/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        model.addAttribute("errorMessage", "Acesso Negado: Apenas ADMINS podem acessar este recurso.");
        model.addAttribute("redirectUrl", "/api/v1/artists/new");
        return "view/error";
    }

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorizedException(UnauthorizedException ex, Model model) {
        model.addAttribute("errorMessage", "É necessário logar ou se cadastrar");
        model.addAttribute("redirectUrl", "/api/v1/users/register");
        return "view/error";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("redirectUrl", "/api/v1/users/register");
        return "view/error";
    }

    @ExceptionHandler(UserLoginException.class)
    public String handleUserLoginException(UserLoginException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("redirectUrl", "/api/v1/users/login");
        return "view/error";
    }

    @ExceptionHandler(UserRegisterException.class)
    public String handleUserRegisterException(UserRegisterException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("redirectUrl", "/api/v1/users/register");
        return "view/error";
    }
}
