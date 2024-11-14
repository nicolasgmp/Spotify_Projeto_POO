package br.com.poofinal.bands_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.poofinal.bands_api.service.artist.ArtistService;

@Controller
@RequestMapping("/api/v1/artists")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @GetMapping("/favorites")
    public String getUserFavArtists(Model model) {
        var artists = artistService.findUserArtists();
        model.addAttribute("artists", artists);
        return "view/artists";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        var artists = artistService.findAllArtists();
        model.addAttribute("artists", artists);
        return "view/artists";
    }

    @GetMapping("/new")
    public String getForm() {
        return "view/artist-form";
    }

    @PostMapping("/new")
    public String saveArtist(@RequestParam String name) {
        artistService.createArtist(name);
        return "redirect:/api/v1/artists/favorites";
    }
}
