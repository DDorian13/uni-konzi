package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Document(collection = "users")
public class User {

    @Id
    @Null
    private final ObjectId id;

    @NotBlank
    @Field("name")
    private final String name;

    @NotBlank
    @Email
    @Field("email")
    private final String email;

    @NotBlank
    @Field("password")
    private final String password;

    @Field("role")
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
        if (id == null)
            return null;
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
