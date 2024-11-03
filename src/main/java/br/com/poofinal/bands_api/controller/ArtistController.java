package br.com.poofinal.bands_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.poofinal.bands_api.service.artist.ArtistService;

@Controller
@RequestMapping("/api/v1/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping("/artist")
    public ModelAndView getArtist(@RequestParam String id, Model model) {
        ModelAndView mv = new ModelAndView("view/artist");
        var artist = artistService.findArtistSpotify(id);
        mv.addObject("artist", artist);
        return mv;
    }
}
