package br.com.poofinal.bands_api.service.user;

import br.com.poofinal.bands_api.dtos.UserLoginRequestDTO;
import br.com.poofinal.bands_api.dtos.UserRegisterDTO;

public interface IUserService {
    String login(UserLoginRequestDTO dto);

    void register(UserRegisterDTO dto);
}