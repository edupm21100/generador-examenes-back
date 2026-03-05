package com.eduardo.examen_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String oldPassword;

    @NotBlank(message = "La contraseña nueva es obligatoria")
    @Size(min = 6, message = "La nueva contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    public String getOldPassword() {return oldPassword;}
    public void setOldPassword(String oldPassword) {this.oldPassword = oldPassword;}

    public String getNewPassword() {return newPassword;}
    public void setNewPassword(String newPassword) {this.newPassword = newPassword;}

    
}
