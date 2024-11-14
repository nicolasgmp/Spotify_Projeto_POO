package br.com.poofinal.bands_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.poofinal.bands_api.exception.user.AccessDeniedException;
import br.com.poofinal.bands_api.exception.user.UnauthorizedException;
import br.com.poofinal.bands_api.models.User;
import br.com.poofinal.bands_api.models.enums.UserRole;
import br.com.poofinal.bands_api.repository.UserRepository;
import br.com.poofinal.bands_api.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/v1/users")
public class AuthController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService service;

    @GetMapping("/login")
    public String getLoginForm() {
        return "view/user-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest req) {
        var user = this.repository.findByUsername(username);
        if (encoder.matches(password, user.get().getPassword())) {
            String token = this.service.generateToken(user.get());
            req.getSession().setAttribute("token", token);
            return "redirect:/api/v1/artists/new";
        }
        return "redirect:/api/v1/users/login";
    }

    @GetMapping("/register")
    public String getRegisterForm() {
        return "view/user-register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String username,
            @RequestParam String password) {
        var user = this.repository.findByUsername(username);
        if (user.isPresent()) {
            throw new RuntimeException("username already exists");
        }
        var newUser = new User();
        newUser.setUsername(username);
        newUser.setName(name);
        newUser.setPassword(encoder.encode(password));
        newUser.setRole(UserRole.BASIC);
        this.repository.save(newUser);

        return "redirect:/api/v1/users/login";
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest req) {
        req.getSession().invalidate();

        return "redirect:/api/v1/users/login";
    }

    @GetMapping("/error/accessDenied")
    public String accessDenied(Model model) {
        throw new AccessDeniedException("Acesso negado. Seu nível de acesso não permite que este recurso seja acessado");
    }

    @GetMapping("/error/unauthorized")
    public String unauthorized(Model model) {
        throw new UnauthorizedException("É necessário logar ou cadastrar");
    }

}
