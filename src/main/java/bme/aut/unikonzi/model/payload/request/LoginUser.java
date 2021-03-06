package bme.aut.unikonzi.model.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class LoginUser {

    @NotBlank
    private final String username;

    @NotBlank
    private final String password;

    public LoginUser(@JsonProperty("username") String username,
                     @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
