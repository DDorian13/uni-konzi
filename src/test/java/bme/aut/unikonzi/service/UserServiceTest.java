package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.exception.UserAlreadyExistsException;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService service;

    @MockBean
    private UserDao repository;

    private ObjectId id = new ObjectId();
    private User userWithoutId = new User(null, "name", "email", "password", Set.of(User.Role.ROLE_USER));
    private User userWithId = new User(id, "name", "email", "password", Set.of(User.Role.ROLE_USER));

    @Test
    @DisplayName("Add user, successful")
    public void addUserSuccessfulTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.empty());
        Mockito.when(repository.findByName("name")).thenReturn(Optional.empty());
        Mockito.when(repository.insert(userWithoutId)).thenReturn(userWithId);

        Optional<User> userOptional = service.addUser(userWithoutId);

        assertThat(userOptional.isPresent()).isEqualTo(true);
        User user = userOptional.get();
        assertThat(user.getId()).isEqualTo(id.toString());
        assertThat(user.getName()).isEqualTo(userWithoutId.getName());
        assertThat(user.getEmail()).isEqualTo(userWithoutId.getEmail());
        assertThat(user.getPassword()).isEqualTo(userWithoutId.getPassword());
        assertThat(user.getRoles()).isEqualTo(userWithoutId.getRoles());
    }

    @Test
    @DisplayName("Add user, email conflict")
    public void addUserEmailConflictTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.of(userWithId));

        assertThrows(UserAlreadyExistsException.class, () -> service.addUser(userWithoutId));
    }

    @Test
    @DisplayName("Add user, name conflict")
    public void addUserNameConflictTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.empty());
        Mockito.when(repository.findByName("name")).thenReturn(Optional.of(userWithId));

        assertThrows(UserAlreadyExistsException.class, () -> service.addUser(userWithoutId));
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsersTest() {
        User user2 = new User(new ObjectId(), "name2", "email2", "password2", Set.of(User.Role.ROLE_USER));
        User user3 = new User(new ObjectId(), "name3", "email3", "password3", Set.of(User.Role.ROLE_USER));
        Mockito.when(repository.findAll(1, 10)).thenReturn(List.of(userWithId, user2, user3));

        assertThat(service.getAllUsers(1, 10)).isEqualTo(List.of(userWithId, user2, user3));
    }

    @Test
    @DisplayName("Get user by id, successful")
    public void getUserByIdSuccessfulTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(userWithId));

        assertThat(service.getUserById(id)).isEqualTo(Optional.of(userWithId));
    }

    @Test
    @DisplayName("Get user by id, not exists")
    public void getUserByIdNotExistsTest() {
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        assertThat(service.getUserById(id)).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Delete user, successful")
    public void deleteUserSuccessfulTest() {
        Mockito.when(repository.deleteById(id)).thenReturn(1);

        assertThat(service.deleteUser(id)).isEqualTo(1);
    }

    @Test
    @DisplayName("Delete user, not exists")
    public void deleteUserNotExistsTest() {
        Mockito.when(repository.deleteById(id)).thenReturn(0);

        assertThat(service.deleteUser(id)).isEqualTo(0);
    }

    @Test
    @DisplayName("Set as admin, successful")
    public void setAsAdminSuccessfulTest() {
        Mockito.when(repository.setAsAdminById(id)).thenReturn(1);

        assertThat(service.setUserAsAdmin(id)).isEqualTo(1);
    }

    @Test
    @DisplayName("Set as admin, not exists")
    public void setAsAdminNotExistsTest() {
        Mockito.when(repository.setAsAdminById(id)).thenReturn(0);

        assertThat(service.setUserAsAdmin(id)).isEqualTo(0);
    }

    @Test
    @DisplayName("Get user by email, successful")
    public void getUserByEmailSuccessfulTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.of(userWithId));

        assertThat(service.getUserByEmail("email")).isEqualTo(Optional.of(userWithId));
    }

    @Test
    @DisplayName("Get user by email, not exists")
    public void getUserByEmailNotExistsTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.empty());

        assertThat(service.getUserByEmail("email")).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Get user by name, successful")
    public void getUserByNameSuccessfulTest() {
        Mockito.when(repository.findByName("email")).thenReturn(Optional.of(userWithId));

        assertThat(service.getUserByName("email")).isEqualTo(Optional.of(userWithId));
    }

    @Test
    @DisplayName("Get user by name, not exists")
    public void getUserByNameNotExistsTest() {
        Mockito.when(repository.findByName("email")).thenReturn(Optional.empty());

        assertThat(service.getUserByName("email")).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Login successful")
    public void loginSuccessfulTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.of(userWithId));

        assertThat(service.loginUser("email", "password")).isEqualTo(Optional.of(userWithId));
    }

    @Test
    @DisplayName("Login wrong email")
    public void loginWrongEmailTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.empty());

        assertThat(service.loginUser("email", "password")).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("Login wrong password")
    public void loginWrongPasswordTest() {
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.of(userWithId));

        assertThat(service.loginUser("email", "password2")).isEqualTo(Optional.empty());
    }
}
