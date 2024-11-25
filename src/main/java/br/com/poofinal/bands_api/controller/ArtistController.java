package br.com.poofinal.bands_api.controller;

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
import br.com.poofinal.bands_api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/v1/artists")
@Tag(name = "Spotify-POO", description = "Operações relacionadas aos artistas")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Realiza a busca dos artistas favoritados pelo usuário", description = "Retorna a lista de artistas favoritados por um usuário autenticado.", responses = {
            @ApiResponse(description = "Lista de artistas", responseCode = "200") })
    @GetMapping("/favorites")
    public String getUserFavArtists(Model model, HttpServletRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var artists = artistService.findUserArtists(auth);

        model.addAttribute("requestURI", req.getRequestURI());
        model.addAttribute("artists", artists);
        return "view/artists";
    }

    @Operation(summary = "Busca todos os artistas cadastrados no DB (acessível apenas para ADMIN)", description = "Retorna todos os artistas cadastrados no banco de dados.", responses = {
            @ApiResponse(description = "Lista de todos os artistas", responseCode = "200") })
    @GetMapping("/all")
    public String getAll(Model model, HttpServletRequest req) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        var artists = artistService.findAllArtists(auth);
        model.addAttribute("requestURI", req.getRequestURI());
        model.addAttribute("artists", artists);
        return "view/artists";
    }

    @Operation(summary = "Retorna o formulário de cadastro de artista", responses = {
            @ApiResponse(description = "Formulário para cadastro de artista", responseCode = "200") })
    @GetMapping("/new")
    public String getForm() {
        return "view/artist-form";
    }

    @Operation(summary = "Cria um novo artista", description = "Cria um artista com o nome fornecido.", responses = {
            @ApiResponse(description = "Redireciona para lista de artistas favoritos", responseCode = "201") })
    @PostMapping("/new")
    public String saveArtist(@RequestParam String name) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        artistService.createArtist(name, auth);
        return "redirect:/api/v1/artists/favorites";
    }

    @Operation(summary = "Remove um artista dos favoritos do usuário", description = "Remove o artista favorito especificado pelo usuário.", responses = {
            @ApiResponse(description = "Redireciona para lista de artistas favoritos", responseCode = "302") })
    @PostMapping("/remove")
    public String removeFromFavorites(@RequestParam String artistName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        userService.removeFavorite(auth.getName(), artistName);
        return "redirect:/api/v1/artists/favorites";
    }
}
