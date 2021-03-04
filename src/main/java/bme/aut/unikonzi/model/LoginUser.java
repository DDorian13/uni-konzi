package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class LoginUser {

    @NotBlank
    private final String email;

    @NotBlank
    private final String password;

    public LoginUser(@JsonProperty("email") String email,
                     @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
