package br.com.poofinal.bands_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var artists = artistService.findUserArtists(auth);
        model.addAttribute("artists", artists);
        return "view/artists";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var artists = artistService.findAllArtists(auth);
        model.addAttribute("artists", artists);
        return "view/artists";
    }

    @GetMapping("/new")
    public String getForm() {
        return "view/artist-form";
    }

    @PostMapping("/new")
    public String saveArtist(@RequestParam String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        artistService.createArtist(name, auth);
        return "redirect:/api/v1/artists/favorites";
    }
}
