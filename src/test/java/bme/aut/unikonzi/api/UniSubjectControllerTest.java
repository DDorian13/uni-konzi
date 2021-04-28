package bme.aut.unikonzi.api;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.helper.TokenMock;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.service.SubjectService;
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

import java.util.Optional;

import static bme.aut.unikonzi.helper.EqualityChecker.areSubjectsEquals;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UniSubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private SubjectService subjectServices;

    @MockBean
    private UniversityService universityService;

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
    @DisplayName("Delete subject, successful")
    public void deleteSubjectSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        university.addSubject(subject);
        Mockito.when(universityService.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.of(university));
        Mockito.when(subjectServices.getSubjectById(any(ObjectId.class)))
                .thenReturn(Optional.of(subject));

        String url = String.format("/api/universities/%s/%s", university.getId(), subject.getId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The subject was deleted successfully")))
                .andReturn();
    }

    @Test
    @DisplayName("Delete subject, university not exists")
    public void deleteSubjectUniversityNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        university.addSubject(subject);
        Mockito.when(universityService.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.empty());
        Mockito.when(subjectServices.getSubjectById(any(ObjectId.class)))
                .thenReturn(Optional.of(subject));

        String url = String.format("/api/universities/%s/%s", university.getId(), subject.getId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the university is invalid")))
                .andReturn();
    }

    @Test
    @DisplayName("Delete subject, subject not exists")
    public void deleteSubjectNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        university.addSubject(subject);
        Mockito.when(universityService.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.of(university));
        Mockito.when(subjectServices.getSubjectById(any(ObjectId.class)))
                .thenReturn(Optional.empty());

        String url = String.format("/api/universities/%s/%s", university.getId(), subject.getId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the subject is invalid")))
                .andReturn();
    }

    @Test
    @DisplayName("Delete subject, no admin privilege")
    public void deleteSubjectNotAsAdminTest() throws Exception {
        String url = String.format("/api/universities/%s/%s", new ObjectId(), new ObjectId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @DisplayName("Add subject to university, successful")
    public void addSubjectSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        System.out.println(university.getSubjects().size());
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        Mockito.when(universityService.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.of(university));
        Mockito.when(subjectServices.addSubject(any(Subject.class)))
                .thenReturn(Optional.of(subject));

        String url = String.format("/api/universities/%s", university.getId());
        String body = SubjectController.subjectsToJson(new String[0], subject);
        MvcResult result = mockMvc.perform(post(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Subject newSubject = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Subject>() {});
        areSubjectsEquals(newSubject, subject);
    }

    @Test
    @DisplayName("Add subject to university, university not exists")
    public void addSubjectUniversityNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        Mockito.when(universityService.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.empty());

        String url = String.format("/api/universities/%s", new ObjectId());
        String body = SubjectController.subjectsToJson(new String[0], subject);
        mockMvc.perform(post(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the university is invalid")));
    }

    @Test
    @DisplayName("Add subject to university, already added")
    public void addSubjectAlreadyAddedTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        University university = new University(new ObjectId(), "name", "country", "city", null);
        Subject subject = new Subject(new ObjectId(), "code", "name", null);
        university.addSubject(subject);
        Mockito.when(universityService.getUniversityById(any(ObjectId.class), any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.of(university));

        String url = String.format("/api/universities/%s", university.getId());
        String body = SubjectController.subjectsToJson(new String[0], subject);
        mockMvc.perform(post(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Subject already exists")));
    }

    @Test
    @DisplayName("Add subject to university, no admin privilege")
    public void addSubjectNotAsAdminTest() throws Exception {
        Subject subject = new Subject(new ObjectId(), "code", "name", null);

        String url = String.format("/api/universities/%s", new ObjectId());
        String body = SubjectController.subjectsToJson(new String[0], subject);
        mockMvc.perform(post(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }
}
