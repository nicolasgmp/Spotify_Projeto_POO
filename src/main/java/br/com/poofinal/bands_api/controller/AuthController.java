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
import br.com.poofinal.bands_api.exception.user.UserLoginException;
import br.com.poofinal.bands_api.exception.user.UserRegisterException;
import br.com.poofinal.bands_api.models.User;
import br.com.poofinal.bands_api.models.enums.UserRole;
import br.com.poofinal.bands_api.repository.UserRepository;
import br.com.poofinal.bands_api.security.TokenService;
import br.com.poofinal.bands_api.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api/v1/users")
public class AuthController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Retorna o formulário de login de usuário", responses = {
            @ApiResponse(description = "Formulário de login", responseCode = "200") })
    @GetMapping("/login")
    public String getLoginForm() {
        return "view/user-login";
    }

    @Operation(summary = "Realiza o login no sistema", description = "Valida as credenciais e gera um token para autenticação", responses = {
            @ApiResponse(description = "Redireciona para a página de novos artistas", responseCode = "200") })
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpServletRequest req) {
        var user = this.repository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserLoginException("Usuário ou senha inválidos");
        }
        if (!encoder.matches(password, user.get().getPassword())) {
            throw new UserLoginException("Usuário ou senha inválidos");
        }
        String token = this.tokenService.generateToken(user.get());
        req.getSession().setAttribute("token", token);
        return "redirect:/api/v1/artists/new";
    }

    @Operation(summary = "Retorna o formulário de cadastro de usuário", responses = {
            @ApiResponse(description = "Formulário de cadastro", responseCode = "200") })
    @GetMapping("/register")
    public String getRegisterForm() {
        return "view/user-register";
    }

    @Operation(summary = "Cadastra um novo usuário no sistema", description = "Cria um novo usuário com um nome, username e senha.", responses = {
            @ApiResponse(description = "Redireciona para a página de login", responseCode = "201") })
    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String username,
            @RequestParam String password) {
        var user = this.repository.findByUsername(username);
        if (user.isPresent()) {
            throw new UserRegisterException("Usuário já existe");
        }
        if (!userService.PasswordMatchesPattern(password)) {
            throw new UserRegisterException(
                    "Senha no padrão errado. Exemplo: Senha1234!");
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
        throw new AccessDeniedException(
                "Acesso negado. Seu nível de acesso não permite que este recurso seja acessado");
    }

    @GetMapping("/error/unauthorized")
    public String unauthorized(Model model) {
        throw new UnauthorizedException("É necessário logar ou cadastrar");
    }
}