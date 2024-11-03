package br.com.poofinal.bands_api.client.login;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LoginRequest(String grantType, String clientId, String clientSecret) {
}