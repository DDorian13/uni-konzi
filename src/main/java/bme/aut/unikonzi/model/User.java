package bme.aut.unikonzi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private String password;

    @Field("role")
    private Set<Role> role = new HashSet<>();

    public User(@JsonProperty("id") ObjectId id,
                @JsonProperty("name") String name,
                @JsonProperty("email") String email,
                @JsonProperty("password") String password,
                @JsonProperty("role") Set<Role> role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
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

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return role;
    }

    public void addRole(Role role) {
        if (this.role.size() == 0) {
            this.role = new HashSet<>();
        }
        this.role.add(role);
    }

    public void setRoles(Set<Role> role) {
        this.role = role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public enum Role {
        ROLE_ADMIN,
        ROLE_USER
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
