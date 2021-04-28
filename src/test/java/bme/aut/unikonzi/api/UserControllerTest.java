package bme.aut.unikonzi.api;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.exception.UserAlreadyExistsException;
import bme.aut.unikonzi.helper.TokenMock;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.model.payload.request.LoginUser;
import bme.aut.unikonzi.security.jwt.AuthEntryPointJwt;
import bme.aut.unikonzi.security.jwt.JwtUtils;
import bme.aut.unikonzi.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @MockBean
    private UserService service;

    private String adminToken = TokenMock.getAdminToken("username");
    private String userToken = TokenMock.getUserToken("username");
    private User admin = TokenMock.admin;
    private User user = TokenMock.user;

    @BeforeEach
    public void init() {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Signup, successful")
    public void signupSuccessfulTest() throws Exception {
        Mockito.when(service.addUser(any(User.class)))
                .thenReturn(Optional.of(user));

        MvcResult result = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        "{ \"name\": \"name\"," +
                        "\"email\": \"my@email.com\"," +
                        "\"password\": \"password\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        User newUser = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<User>() {});
        assertThat(newUser).isEqualTo(user);
    }

    @Test
    @DisplayName("Signup, username is taken")
    public void signupUsernameTakenTest() throws Exception {
        String errorMsg = "User with this username already exists";
        Mockito.when(service.addUser(any(User.class)))
                .thenThrow(new UserAlreadyExistsException(errorMsg));

        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"name\"," +
                        "\"email\": \"my@email.com\"," +
                        "\"password\": \"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(errorMsg)));
    }

    @Test
    @DisplayName("Signup, email is taken")
    public void signupEmailTakenTest() throws Exception {
        String errorMsg = "User with this email already exists";
        Mockito.when(service.addUser(any(User.class)))
                .thenThrow(new UserAlreadyExistsException(errorMsg));

        mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"name\"," +
                        "\"email\": \"my@email.com\"," +
                        "\"password\": \"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(errorMsg)));
    }

    @Test
    @DisplayName("Get all users, successful")
    public void getAllUsersSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.getAllUsers(any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(user, admin));

        MvcResult result = mockMvc.perform(get("/api/users")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<User> users = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<User>>() {});
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0)).isEqualTo(user);
        assertThat(users.get(1)).isEqualTo(admin);
    }

    @Test
    @DisplayName("Get all users, no admin privilege")
    public void getAllUsersNotAsAdminTest() throws Exception {
        mockMvc.perform(get("/api/users")
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get user by id, successful")
    public void getUserByIdSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.getUserById(any(ObjectId.class)))
                .thenReturn(Optional.of(user));

        MvcResult result = mockMvc.perform(get("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        User _user = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<User>() {});
        assertThat(_user).isEqualTo(user);
    }

    @Test
    @DisplayName("Get user by id, not exists")
    public void getUserByIdNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.getUserById(any(ObjectId.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User with the given id does not exists")));
    }

    @Test
    @DisplayName("Get user by id, no admin privilege")
    public void getUserByIdNotAsAdminTest() throws Exception {
        mockMvc.perform(get("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Delete user, successful")
    public void deleteUserSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.deleteUser(any(ObjectId.class)))
                .thenReturn(1);

        mockMvc.perform(delete("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The user was deleted successfully")));
    }

    @Test
    @DisplayName("Delete user, not exists")
    public void deleteUserNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.deleteUser(any(ObjectId.class)))
                .thenReturn(0);

        mockMvc.perform(delete("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("User with the given id does not exists")));
    }

    @Test
    @DisplayName("Delete user, no admin privilege")
    public void deleteUserNotAsAdminTest() throws Exception {
        mockMvc.perform(delete("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Set user as admin, successful")
    public void setUserAsAdminSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.setUserAsAdmin(any(ObjectId.class)))
                .thenReturn(1);

        mockMvc.perform(patch("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Set user as admin, not exists")
    public void setUserAsAdminNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.setUserAsAdmin(any(ObjectId.class)))
                .thenReturn(0);

        mockMvc.perform(patch("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Set user as admin, no admin privilege")
    public void setUserAsAdminNotAsAdminTest() throws Exception {
        mockMvc.perform(patch("/api/users/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isForbidden());
    }

    // Not working properly
    //@Test
    //@DisplayName("Login successful")
    public void loginSuccessfulTest() throws Exception {
        LoginUser loginUser = new LoginUser("username", "pass");

        mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("username")));
    }
}
