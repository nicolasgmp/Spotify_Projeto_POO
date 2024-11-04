package br.com.poofinal.bands_api.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import br.com.poofinal.bands_api.exception.artist.ArtistAlreadyExistsException;
import br.com.poofinal.bands_api.exception.artist.ArtistNotFoundException;

@ControllerAdvice
public class ArtistExceptionHandler {
    
    @ExceptionHandler(ArtistNotFoundException.class)
    public ModelAndView handleArtistNotFoundException(ArtistNotFoundException ex) {
        ModelAndView mv = new ModelAndView("view/artist-error");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }

    @ExceptionHandler(ArtistAlreadyExistsException.class)
    public ModelAndView handleArtistAlreadyExists(ArtistAlreadyExistsException ex) {
        ModelAndView mv = new ModelAndView("view/artist-error");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }
}
