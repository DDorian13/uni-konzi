package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.AppointmentDao;
import bme.aut.unikonzi.model.Appointment;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class AppointmentServiceTest {

    @Autowired
    private AppointmentService service;

    @MockBean
    private AppointmentDao repository;

    private ObjectId id1;
    private ObjectId id2;
    private Appointment apt;

    @BeforeEach
    public void initAll() {
        id1 = new ObjectId();
        id2 = new ObjectId();
        apt = new Appointment(new ObjectId(), id1, id2,
                new Date(), 60, "description", "This is the location");
        Appointment apt2 = new Appointment(new ObjectId(), id2, id1,
                new Date(), 60, "description", "This is the location2");

        Mockito.when(repository.getAppointments(id1, id2)).thenReturn(List.of(apt, apt2));
        Mockito.when(repository.getAppointments(id2, id1)).thenReturn(List.of(apt, apt2));
        Mockito.when(repository.insert(apt)).thenReturn(apt);
    }

    @Test
    @DisplayName("Add test")
    public void addTest() {
        Appointment newApt = service.add(apt);
        assertThat(newApt.getId()).isEqualTo(apt.getId());
        assertThat(newApt.getCreatorId()).isEqualTo(apt.getCreatorId());
        assertThat(newApt.getParticipantId()).isEqualTo(apt.getParticipantId());
        assertThat(newApt.getDate()).isEqualTo(apt.getDate());
        assertThat(newApt.getLength()).isEqualTo(apt.getLength());
        assertThat(newApt.getDescription()).isEqualTo(apt.getDescription());
        assertThat(newApt.getLocation()).isEqualTo(apt.getLocation());
    }

    @Test
    @DisplayName("Get appointments test")
    public void getTest() {
        List<Appointment> appointments = service.getAllBetweenTwoUser(id1, id2);
        assertThat(appointments.size()).isEqualTo(2);
        assertThat(appointments.get(0).getLocation()).isEqualTo("This is the location");
        assertThat(appointments.get(1).getLocation()).isEqualTo("This is the location2");
    }
}
