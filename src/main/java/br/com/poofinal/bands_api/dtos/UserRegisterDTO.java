package br.com.poofinal.bands_api.dtos;

import br.com.poofinal.bands_api.models.enums.UserRole;

public record UserRegisterDTO(String username, String password, UserRole role) {

}
