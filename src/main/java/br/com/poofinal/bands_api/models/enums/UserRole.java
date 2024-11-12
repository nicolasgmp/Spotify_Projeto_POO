package br.com.poofinal.bands_api.models.enums;

public enum UserRole {
    ADMIN("admin"),
    BASIC("basic");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return this.role;
    }
}
