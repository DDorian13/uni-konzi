package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Document(collection = "users")
public class User {

    @Id
    private final ObjectId id;

    @NotBlank
    private final String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private Role role;

    public User(@JsonProperty("id") ObjectId id,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("password") String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        role = Role.User;
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public enum Role {
        Admin,
        User
    }
}
