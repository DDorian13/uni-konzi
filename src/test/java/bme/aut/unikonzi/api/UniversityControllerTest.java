package bme.aut.unikonzi.api;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.helper.TokenMock;
import bme.aut.unikonzi.model.University;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.service.UniversityService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static bme.aut.unikonzi.helper.EqualityChecker.areUniversitiesEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UniversityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private UniversityService service;

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
    @DisplayName("Get all universities")
    public void getAllUniversitiesTest() throws Exception {
        University university1 = new University(new ObjectId(), "name1", "country1", "city1", null);
        University university2 = new University(new ObjectId(), "name2", "country2", "city2", null);
        Mockito.when(service.getAllUniversities(any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(university1, university2));

        MvcResult result = mockMvc.perform(get("/api/universities?page=1&limit=10")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<University> universities = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<University>>() {});
        assertThat(universities.size()).isEqualTo(2);
        University _university1 = universities.get(0);
        areUniversitiesEquals(_university1, university1);
        University _university2 = universities.get(1);
        areUniversitiesEquals(_university2, university2);
    }

    @Test
    @DisplayName("Add university, successful")
    public void addUniversitySuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Mockito.when(service.addUniversity(any(University.class)))
                .thenReturn(Optional.of(university));

        String body = UniversityController.universitiesToJson(false, university);
        MvcResult result = mockMvc.perform(post("/api/universities")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        University _university = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<University>() {});
        areUniversitiesEquals(_university, university);
    }

    @Test
    @DisplayName("Add university, already exists")
    public void addUniversityAlreadyExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Mockito.when(service.addUniversity(any(University.class)))
                .thenReturn(Optional.empty());

        String body = UniversityController.universitiesToJson(false, university);
        mockMvc.perform(post("/api/universities")
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("University already exists")));
    }

    @Test
    @DisplayName("Add university, no admin privilege")
    public void addUniversityNotAsAdminTest() throws Exception {
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Mockito.when(service.addUniversity(any(University.class)))
                .thenReturn(Optional.empty());

        String body = UniversityController.universitiesToJson(false, university);
        mockMvc.perform(post("/api/universities")
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get university by id, successful")
    public void getUniversityByIdSuccessfulTest() throws Exception {
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Mockito.when(service.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.of(university));

        MvcResult result = mockMvc.perform(get("/api/universities/" + university.getId())
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        University _university = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<University>() {});
        areUniversitiesEquals(_university, university);
    }

    @Test
    @DisplayName("Get university by id, not exists")
    public void getUniversityByIdNotExistsTest() throws Exception {
        Mockito.when(service.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/universities/" + new ObjectId())
                .header("Authorization", userToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the university is invalid")));
    }

    @Test
    @DisplayName("Delete university, successful")
    public void deleteUniversitySuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.deleteUniversity(any(ObjectId.class))).thenReturn(1);

        mockMvc.perform(delete("/api/universities/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The university was deleted successfully")));
    }

    @Test
    @DisplayName("Delete university, not exists")
    public void deleteUniversityNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Mockito.when(service.deleteUniversity(any(ObjectId.class))).thenReturn(0);

        mockMvc.perform(delete("/api/universities/" + new ObjectId())
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the university is invalid")));
    }

    @Test
    @DisplayName("Delete university, no admin privilege")
    public void deleteUniversityNotAsAdminTest() throws Exception {
        mockMvc.perform(delete("/api/universities/" + new ObjectId())
                .header("Authorization", userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Update university, successful")
    public void updateUniversitySuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Mockito.when(service.updateUniversityById(any(ObjectId.class), any(University.class)))
                .thenReturn(Optional.of(university));

        String url = String.format("/api/universities/%s", university.getId());
        String body = UniversityController.universitiesToJson(true, university);
        MvcResult result = mockMvc.perform(patch(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        University _university = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<University>() {});
        areUniversitiesEquals(_university, university);
    }

    @Test
    @DisplayName("Update university, not exists")
    public void updateUniversityNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Mockito.when(service.updateUniversityById(any(ObjectId.class), any(University.class)))
                .thenReturn(Optional.empty());

        String url = String.format("/api/universities/%s", university.getId());
        String body = UniversityController.universitiesToJson(true, university);
        mockMvc.perform(patch(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the university is invalid")));
    }

    @Test
    @DisplayName("Update university, no admin privilege")
    public void updateUniversityNotAsAdminTest() throws Exception {
        University university = new University(new ObjectId(), "name", "country", "city", null);

        String url = String.format("/api/universities/%s", university.getId());
        String body = UniversityController.universitiesToJson(true, university);
        mockMvc.perform(patch(url)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get universities by name")
    public void getUniversitiesByNameTest() throws Exception {
        University university1 = new University(new ObjectId(), "name1", "country1", "city1", null);
        University university2 = new University(new ObjectId(), "name2", "country2", "city2", null);
        Mockito.when(service.getUniversitiesByNameRegex(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(university1, university2));

        MvcResult result = mockMvc.perform(get("/api/universities/search?nameLike=name&page=1&limit=10")
                .header("Authorization", userToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<University> universities = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<University>>() {});
        assertThat(universities.size()).isEqualTo(2);
        University _university1 = universities.get(0);
        areUniversitiesEquals(_university1, university1);
        University _university2 = universities.get(1);
        areUniversitiesEquals(_university2, university2);
    }
}
