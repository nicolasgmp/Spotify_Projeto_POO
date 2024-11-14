package br.com.poofinal.bands_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.poofinal.bands_api.models.User;
import br.com.poofinal.bands_api.models.enums.UserRole;
import br.com.poofinal.bands_api.repository.UserRepository;
import br.com.poofinal.bands_api.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/v1/users")
public class AuthController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService service;

    @GetMapping("/login")
    public ModelAndView getLoginForm() {
        return new ModelAndView("view/user-login");
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password, HttpServletRequest req) {
        var user = this.repository.findByUsername(username);
        if (encoder.matches(password, user.get().getPassword())) {
            String token = this.service.generateToken(user.get());
            req.getSession().setAttribute("token", token);
            return new ModelAndView("redirect:/api/v1/artists/new");
        }
        return new ModelAndView("redirect:/api/v1/users/login");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterForm() {
        return new ModelAndView("view/user-register");
    }

    @PostMapping("/register")
    public ModelAndView register(@RequestParam String name, @RequestParam String username,
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

        return new ModelAndView("redirect:/api/v1/users/login");
    }

    @PostMapping("/logout")
    public ModelAndView logout(HttpServletRequest req) {
        req.getSession().invalidate();

        return new ModelAndView("redirect:/api/v1/users/login");
    }

}
