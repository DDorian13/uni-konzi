package bme.aut.unikonzi.api;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.helper.TokenMock;
import bme.aut.unikonzi.model.Comment;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.service.SubjectService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Optional;

import static bme.aut.unikonzi.helper.EqualityChecker.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDao userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @MockBean
    private SubjectService service;

    @MockBean
    private UserService userService;

    private String adminToken = TokenMock.getAdminToken("username");
    private String userToken = TokenMock.getUserToken("username");
    private User admin = TokenMock.admin;
    private User user = TokenMock.user;
    private String invalidSubjectId = "The id of the subject is invalid";

    @BeforeEach
    public void init() {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Update subject, successful")
    public void updateSubjectSuccessfulTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        ObjectId id = new ObjectId();
        Subject subject = new Subject(id, "code", "name", null);
        Mockito.when(service.updateSubjectById(any(ObjectId.class), any(Subject.class)))
                .thenReturn(Optional.of(subject));

        String body = SubjectController.subjectsToJson(new String[]{"tutors", "pupils"}, subject);
        MvcResult result = mockMvc.perform(patch(String.format("/api/subjects/%s", id.toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .header("Authorization", adminToken))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Subject updatedSubject = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Subject>() {});
        areSubjectsEquals(updatedSubject, subject);
    }

    @Test
    @DisplayName("Update subject, not exists")
    public void updateSubjectNotExistsTest() throws Exception {
        Mockito.when(userRepository.findByName(any(String.class)))
                .thenReturn(Optional.of(admin));
        ObjectId subjectId = new ObjectId();
        Subject subject = new Subject(subjectId, "code", "name", null);
        Mockito.when(service.updateSubjectById(any(ObjectId.class), any(Subject.class)))
                .thenReturn(Optional.empty());

        String body = SubjectController.subjectsToJson(new String[0], subject);
        mockMvc.perform(patch("/api/subjects/" + subjectId)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("Update subject, no admin privilege")
    public void updateSubjectNotAsAdminTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        Subject subject = new Subject(subjectId, "code", "name", null);

        String body = SubjectController.subjectsToJson(new String[0], subject);
        mockMvc.perform(patch("/api/subjects/" + subjectId)
                .header("Authorization", userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Get with comments, successful")
    public void getWithCommentsSuccessfulTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        ObjectId commentId = new ObjectId();
        Subject subject = new Subject(subjectId, "code", "name", null);
        Comment comment = new Comment(commentId, admin, "comment");
        subject.addComment(comment);
        Mockito.when(service.getSubjectById(any(ObjectId.class))).thenReturn(Optional.of(subject));

        MvcResult result = mockMvc.perform(get(String.format("/api/subjects/%s/comments", subjectId))
                .header("Authorization", adminToken))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Subject subjectWithComments = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Subject>() {});
        areSubjectsEquals(subjectWithComments, subject);
        Comment _comment = subjectWithComments.getComments().get(0);
        assertThat(_comment.getId()).isEqualTo(comment.getId());
        assertThat(_comment.getUser()).isEqualTo(comment.getUser());
        assertThat(_comment.getText()).isEqualTo(comment.getText());
    }

    @Test
    @DisplayName("Get with comment, subject not exists")
    public void getWithCommentsNotExistsTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        Mockito.when(service.getSubjectById(any(ObjectId.class))).thenReturn(Optional.empty());

        String url = String.format("/api/subjects/%s/comments", subjectId);
        mockMvc.perform(get(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("New comment, successful")
    public void newCommentSuccessfulTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        ObjectId commentId = new ObjectId();
        Comment comment = new Comment(commentId, admin, "comment");
        Mockito.when(service.addCommentToSubject(any(ObjectId.class), any(Comment.class)))
                .thenReturn(Optional.of(comment));
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));

        MvcResult result = mockMvc.perform(post("/api/subjects/" + subjectId.toString() + "/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", adminToken)
                .content(mapper.writeValueAsString(comment)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Comment newComment = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Comment>() {});
        assertThat(newComment.getId()).isEqualTo(comment.getId());
        assertThat(newComment.getUser()).isEqualTo(comment.getUser());
        assertThat(newComment.getText()).isEqualTo(comment.getText());
    }

    @Test
    @DisplayName("New comment, subject not exists")
    public void newCommentNotExistsTest() throws Exception {
        Comment comment = new Comment(new ObjectId(), admin, "text");
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.addCommentToSubject(any(ObjectId.class), any(Comment.class))).thenReturn(Optional.empty());

        String url = String.format("/api/subjects/%s/comments", new ObjectId());
        mockMvc.perform(post(url)
                .header("Authorization", adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(comment)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("Get with tutors, successful")
    public void getWithTutorsSuccessfulTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        Subject subject = new Subject(subjectId, "code", "name", null);
        subject.addTutor(admin);
        Mockito.when(service.getSubjectById(any(ObjectId.class))).thenReturn(Optional.of(subject));

        String url = String.format("/api/subjects/%s/tutors", subjectId);
        MvcResult result = mockMvc.perform(get(url)
                .header("Authorization", adminToken))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Subject subjectWithTutors = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Subject>() {});
        areSubjectsEquals(subjectWithTutors, subject);
        assertThat(subjectWithTutors.getTutors().contains(admin)).isEqualTo(true);
    }

    @Test
    @DisplayName("Get with tutors, subject not exists")
    public void getWithTutorsNotExistsTest() throws Exception {
        Mockito.when(service.getSubjectById(any(ObjectId.class))).thenReturn(Optional.empty());

        String url = String.format("/api/subjects/%s/tutors", new ObjectId());
        mockMvc.perform(get(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("New tutor, successful")
    public void newTutorSuccessfulTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.addTutorOrPupilToSubject(
                any(ObjectId.class), any(User.class), any(String.class)
        )).thenReturn(true);

        mockMvc.perform(post("/api/subjects/" + subjectId.toString() + "/tutors")
                .header("Authorization", adminToken))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tutor added")));
    }

    @Test
    @DisplayName("New tutor, subject not exists")
    public void newTutorNotExistsTest() throws Exception {
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.addTutorOrPupilToSubject(any(ObjectId.class), any(User.class), any(String.class)))
                .thenReturn(false);

        String url = String.format("/api/subjects/%s/tutors", new ObjectId());
        mockMvc.perform(post(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("Get with pupils, successful")
    public void getWithPupilsSuccessfulTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        Subject subject = new Subject(subjectId, "code", "name", null);
        subject.addPupil(admin);
        Mockito.when(service.getSubjectById(any(ObjectId.class))).thenReturn(Optional.of(subject));

        String url = String.format("/api/subjects/%s/pupils", subjectId);
        MvcResult result = mockMvc.perform(get(url)
                .header("Authorization", adminToken))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Subject subjectWithPupils = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Subject>() {});
        areSubjectsEquals(subjectWithPupils, subject);
        assertThat(subjectWithPupils.getPupils().contains(admin)).isEqualTo(true);
    }

    @Test
    @DisplayName("Get with pupils, subject not exists")
    public void getWithPupilsNotExistsTest() throws Exception {
        Mockito.when(service.getSubjectById(any(ObjectId.class))).thenReturn(Optional.empty());

        String url = String.format("/api/subjects/%s/pupils", new ObjectId());
        mockMvc.perform(get(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("New pupil, successful")
    public void newPupilSuccessfulTest() throws Exception {
        ObjectId subjectId = new ObjectId();
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.addTutorOrPupilToSubject(
                any(ObjectId.class), any(User.class), any(String.class)
        )).thenReturn(true);

        String url = String.format("/api/subjects/%s/pupils", subjectId);
        mockMvc.perform(post(url)
                .header("Authorization", adminToken))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Pupil added")));
    }

    @Test
    @DisplayName("New pupil, subject not exists")
    public void newPupilNotExistsTest() throws Exception {
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.addTutorOrPupilToSubject(any(ObjectId.class), any(User.class), any(String.class)))
                .thenReturn(false);

        String url = String.format("/api/subjects/%s/pupils", new ObjectId());
        mockMvc.perform(post(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString(invalidSubjectId)));
    }

    @Test
    @DisplayName("Remove pupil, successful")
    public void removePupilSuccessfulTest() throws Exception {
        Mockito.when(userService.getUserById(any(ObjectId.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.removeUserFromSubjectPupils(any(ObjectId.class), any(User.class)))
                .thenReturn(true);

        String url = String.format("/api/subjects/%s/pupils/%s", new ObjectId(), new ObjectId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("Pupil removed")));
    }

    @Test
    @DisplayName("Remove pupil, user not exists")
    public void removePupilUserNotExistsTest() throws Exception {
        Mockito.when(userService.getUserById(any(ObjectId.class))).thenReturn(Optional.empty());

        String url = String.format("/api/subjects/%s/pupils/%s", new ObjectId(), new ObjectId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the user is invalid")));
    }

    @Test
    @DisplayName("Remove pupil, subject not exists or user not pupil")
    public void removePupilFailTest() throws Exception {
        Mockito.when(userService.getUserById(any(ObjectId.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.removeUserFromSubjectPupils(any(ObjectId.class), any(User.class)))
                .thenReturn(false);

        String url = String.format("/api/subjects/%s/pupils/%s", new ObjectId(), new ObjectId());
        mockMvc.perform(delete(url)
                .header("Authorization", adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("The id of the subject is invalid or user was not a pupil")));
    }

    @Test
    @DisplayName("Tutor of following subjects")
    public void tutorOfTest() throws Exception {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Subject subject1 = new Subject(id1, "code1", "name1", null);
        Subject subject2 = new Subject(id2, "code2", "name2", null);
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.tutorOrPupilOf(any(String.class), any(User.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(subject1, subject2));

        MvcResult result = mockMvc.perform(get("/api/subjects/tutor-of")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Subject> subjects = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Subject>>() {});
        assertThat(subjects.size()).isEqualTo(2);
        Subject _subject1 = subjects.get(0);
        areSubjectsEquals(_subject1, subject1);
        Subject _subject2 = subjects.get(1);
        areSubjectsEquals(_subject2, subject2);
    }

    @Test
    @DisplayName("Pupil of following subjects")
    public void pupilOfTest() throws Exception {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Subject subject1 = new Subject(id1, "code1", "name1", null);
        Subject subject2 = new Subject(id2, "code2", "name2", null);
        Mockito.when(userService.getUserByName(any(String.class))).thenReturn(Optional.of(admin));
        Mockito.when(service.tutorOrPupilOf(any(String.class), any(User.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(subject1, subject2));

        MvcResult result = mockMvc.perform(get("/api/subjects/pupil-of")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Subject> subjects = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Subject>>() {});
        assertThat(subjects.size()).isEqualTo(2);
        Subject _subject1 = subjects.get(0);
        areSubjectsEquals(_subject1, subject1);
        Subject _subject2 = subjects.get(1);
        areSubjectsEquals(_subject2, subject2);
    }

    @Test
    @DisplayName("Search subjects")
    public void searchSubjectsTest() throws Exception {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Subject subject1 = new Subject(id1, "code1", "name1", null);
        Subject subject2 = new Subject(id2, "code2", "name2", null);
        Mockito.when(service.getSubjectsByName(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(subject1, subject2));

        MvcResult result = mockMvc.perform(get("/api/subjects/search?nameLike=name&page=1&limit=10")
                .header("Authorization", adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<Subject> subjects = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Subject>>() {});
        assertThat(subjects.size()).isEqualTo(2);
        Subject _subject1 = subjects.get(0);
        areSubjectsEquals(_subject1, subject1);
        Subject _subject2 = subjects.get(1);
        areSubjectsEquals(_subject2, subject2);
    }
}