package bme.aut.unikonzi.model;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTest {
    private User user;

    @BeforeEach
    public void init() {
        user = new User(new ObjectId("606440c76413383605eb3dd0"), "Name", "email@email.com",
                "password", Collections.emptySet());
    }

    @DisplayName("Constructor")
    @Test
    public void constructorTest() {
        assertThat(user.getId()).isEqualTo("606440c76413383605eb3dd0");
        assertThat(user.getName()).isEqualTo("Name");
        assertThat(user.getEmail()).isEqualTo("email@email.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getRoles()).isEqualTo(Collections.emptySet());
    }

    @DisplayName("Add Role")
    @Test
    public void addRoleTest() {
        assertThat(user.getRoles()).isEqualTo(Collections.emptySet());
        user.addRole(User.Role.ROLE_ADMIN);
        assertThat(user.getRoles()).isEqualTo(Set.of(User.Role.ROLE_ADMIN));
        user.addRole(User.Role.ROLE_USER);
        assertThat(user.getRoles()).isEqualTo(Set.of(User.Role.ROLE_ADMIN, User.Role.ROLE_USER));
    }

    @DisplayName("Set Role")
    @Test
    public void setRoleTest() {
        assertThat(user.getRoles()).isEqualTo(Collections.emptySet());
        user.setRole(Set.of(User.Role.ROLE_USER));
        assertThat(user.getRoles()).isEqualTo(Set.of(User.Role.ROLE_USER));
    }
}
