package com.DriveAuto.user_service.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateDto {

        @Size(min = 3, max = 20, message = "Username entre 3 et 20 charactèrs")
        private String username;

        @Size(min = 8, message = "Password 8 charactèrs ")
        private String password;

        @Email(message = "Email doit être valide")
        private String email;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

}
