package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.UniKonziApplication;
import bme.aut.unikonzi.dao.impl.MongoUserDao;
import bme.aut.unikonzi.model.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class UserDaoTest {

    @Autowired
    private MongoUserDao dao;

    @BeforeEach
    private void init() {
        dao.findAll(1, 10).forEach(user -> dao.deleteById(new ObjectId(user.getId())));
    }

    @DisplayName("Insert")
    @Test
    public void insertTest() {
        ObjectId id = new ObjectId();
        User user = new User(id, "My Name", "a@b.c", "password", Set.of(User.Role.ROLE_USER));
        assertThat(dao.findAll(1, 10).size()).isEqualTo(0);
        assertThat(dao.insert(user)).isEqualTo(user);
        assertThat(dao.findAll(1, 10).size()).isEqualTo(1);
    }

    @DisplayName("Delete")
    @Test
    public void deleteTest() {
        ObjectId id = new ObjectId();
        User user = new User(id, "na", "a@b.c", "pass", Set.of(User.Role.ROLE_ADMIN));
        dao.insert(user);
        assertThat(dao.findAll(1, 10).size()).isEqualTo(1);
        assertThat(dao.deleteById(id)).isEqualTo(1);
        assertThat(dao.deleteById(new ObjectId())).isEqualTo(0);
        assertThat(dao.findAll(1, 10).size()).isEqualTo(0);
    }

    @DisplayName("Find all")
    @Test
    public void findAllTest() {
        User user = new User(new ObjectId(), "na", "a@b.c", "pass", Set.of(User.Role.ROLE_ADMIN));
        User user2 = new User(new ObjectId(), "nas", "as@b.c", "pass", Set.of(User.Role.ROLE_USER));
        dao.insert(user);
        dao.insert(user2);
        assertThat(dao.findAll(1, 10)).isEqualTo(List.of(user, user2));
    }

    @DisplayName("Find by id")
    @Test
    public void findByIdTest() {
        ObjectId id = new ObjectId();
        User user = new User(new ObjectId(), "na", "a@b.c", "pass", Set.of(User.Role.ROLE_ADMIN));
        User user2 = new User(id, "nas", "as@b.c", "pass", Set.of(User.Role.ROLE_USER));
        dao.insert(user);
        dao.insert(user2);
        assertThat(dao.findById(id)).isEqualTo(Optional.of(user2));
        assertThat(dao.findById(new ObjectId())).isEqualTo(Optional.empty());
    }

    @DisplayName("Set as admin by id")
    @Test
    public void setAsAdminTest() {
        ObjectId id = new ObjectId();
        User user = new User(id, "na", "a@b.c", "pass", Set.of(User.Role.ROLE_USER));
        dao.insert(user);
        assertThat(dao.setAsAdminById(id)).isEqualTo(1);
        assertThat(dao.setAsAdminById(new ObjectId())).isEqualTo(0);
        assertThat(dao.findById(id).get().getRoles().contains(User.Role.ROLE_ADMIN)).isEqualTo(true);
    }

    @DisplayName("Find by name")
    @Test
    public void findByNameTest() {
        User user2 = new User(new ObjectId(), "nas", "as@b.c", "pass", Set.of(User.Role.ROLE_USER));
        User user = new User(new ObjectId(), "My Name", "a@b.c", "pass", Set.of(User.Role.ROLE_ADMIN));
        dao.insert(user2);
        dao.insert(user);
        assertThat(dao.findByName("My Name")).isEqualTo(Optional.of(user));
    }

    @DisplayName("Find by email")
    @Test
    public void findByEmailTest() {
        User user = new User(new ObjectId(), "My Name", "a@b.c", "pass", Set.of(User.Role.ROLE_ADMIN));
        User user2 = new User(new ObjectId(), "nas", "as@b.c", "pass", Set.of(User.Role.ROLE_USER));
        dao.insert(user);
        dao.insert(user2);
        assertThat(dao.findByEmail("a@b.c")).isEqualTo(Optional.of(user));
    }
}
