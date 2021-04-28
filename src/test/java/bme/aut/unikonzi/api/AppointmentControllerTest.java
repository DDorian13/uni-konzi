package bme.aut.unikonzi.api;

import bme.aut.unikonzi.dao.UserDao;
import bme.aut.unikonzi.helper.TokenMock;
import bme.aut.unikonzi.model.Appointment;
import bme.aut.unikonzi.service.AppointmentService;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService service;

    @MockBean
    private UserDao userRepository;

    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        Mockito.when(userRepository.findByName(any(String.class))).thenReturn(
                Optional.of(TokenMock.user));
    }

    @Test
    @DisplayName("Appointments of two users")
    public void getAppointmentsBetweenUsersTest() throws Exception {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Date now = new Date();
        ObjectId app_id = new ObjectId();
        ObjectId app_id2 = new ObjectId();
        Appointment app1 = new Appointment(app_id, id1, id2, now,
                60, "description", "location");
        Appointment app2 = new Appointment(app_id2, id2, id1, now,
                40, "description2", "location2");
        Mockito.when(service.getAllBetweenTwoUser(id1, id2)).thenReturn(List.of(app1, app2));

        MvcResult result = mockMvc.perform(get(String.format("/api/appointments/%s/%s", id1, id2))
                .header("Authorization", TokenMock.getAdminToken("username")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Appointment> appointments = this.mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Appointment>>() {});

        assertThat(appointments.size()).isEqualTo(2);
        Appointment returnedApp1 = appointments.get(0);
        assertThat(returnedApp1.getId()).isEqualTo(app_id.toString());
        assertThat(returnedApp1.getCreatorId()).isEqualTo(id1.toString());
        assertThat(returnedApp1.getParticipantId()).isEqualTo(id2.toString());
        assertThat(returnedApp1.getLength()).isEqualTo(60);
        assertThat(returnedApp1.getDescription()).isEqualTo("description");
        assertThat(returnedApp1.getLocation()).isEqualTo("location");

        Appointment returnedApp2 = appointments.get(1);
        assertThat(returnedApp2.getId()).isEqualTo(app_id2.toString());
        assertThat(returnedApp2.getCreatorId()).isEqualTo(id2.toString());
        assertThat(returnedApp2.getParticipantId()).isEqualTo(id1.toString());
        assertThat(returnedApp2.getLength()).isEqualTo(40);
        assertThat(returnedApp2.getDescription()).isEqualTo("description2");
        assertThat(returnedApp2.getLocation()).isEqualTo("location2");
    }

    @Test
    @DisplayName("New appointment")
    public void newAppointmentTest() throws Exception {
        ObjectId appointmentId = new ObjectId();
        ObjectId creatorId = new ObjectId();
        ObjectId participantId = new ObjectId();
        Date date = new Date();
        int length = 45;
        String description = "description";
        String location = "location";
        Appointment appointment = new Appointment(appointmentId, creatorId, participantId, date, length, description, location);
        Mockito.when(service.add(any(Appointment.class))).thenReturn(appointment);

        MvcResult result = mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(String.format("{" +
                        "\"creatorId\": \"%s\"," +
                        "\"participantId\": \"%s\"," +
                        "\"date\": %s," +
                        "\"length\": %d," +
                        "\"description\": \"%s\"," +
                        "\"location\": \"%s\"}", creatorId, participantId, date.getTime(), length, description, location))
                .header("Authorization", TokenMock.getAdminToken("username")))
                .andExpect(status().isCreated())
                .andReturn();

        Appointment newAppointment = this.mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<Appointment>() {});

        assertThat(newAppointment).isEqualTo(appointment);
    }
}
