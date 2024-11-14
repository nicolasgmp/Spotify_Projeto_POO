package br.com.poofinal.bands_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.poofinal.bands_api.service.artist.ArtistService;

@Controller
@RequestMapping("/api/v1/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping("/favorites")
    public ModelAndView getUserFavArtists() {
        ModelAndView mv = new ModelAndView("view/artists");
        var artists = artistService.findUserArtists();
        mv.addObject("artists", artists);
        return mv;
    }

    @GetMapping("/all")
    public ModelAndView getAll() {
        ModelAndView mv = new ModelAndView("view/artists");
        var artists = artistService.findAllArtists();
        mv.addObject("artists", artists);
        return mv;
    }

    @GetMapping("/new")
    public ModelAndView getForm() {
        return new ModelAndView("view/artist-form");
    }

    @PostMapping("/new")
    public ModelAndView saveArtist(@RequestParam String name) {
        artistService.createArtist(name); 
        return new ModelAndView("redirect:/api/v1/artists/favorites");
    }
}
