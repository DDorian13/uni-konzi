package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.dao.impl.MongoAppointmentDao;
import bme.aut.unikonzi.model.Appointment;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class AppointmentDaoTest {

    @Autowired
    private MongoAppointmentDao repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static String collectionName = "appointments";

    @BeforeEach
    private void init() {
        mongoTemplate.findAll(Appointment.class, collectionName).forEach(a -> mongoTemplate.remove(a));
    }

    @Test
    @DisplayName("New appointment test")
    public void insertTest() {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Date date = new Date();
        Appointment apt = new Appointment(null, id1, id2, date, 60, "This is the description", "Location");
        apt = repository.insert(apt);

        Appointment apt2 = mongoTemplate.findAll(Appointment.class, collectionName).get(0);
        assertThat(apt2.getId()).isEqualTo(apt.getId());
        assertThat(apt2.getCreatorId()).isEqualTo(apt.getCreatorId());
        assertThat(apt2.getParticipantId()).isEqualTo(apt.getParticipantId());
        assertThat(apt2.getDate()).isEqualTo(apt.getDate());
        assertThat(apt2.getLength()).isEqualTo(apt.getLength());
        assertThat(apt2.getDescription()).isEqualTo(apt.getDescription());
        assertThat(apt2.getLocation()).isEqualTo(apt.getLocation());
    }

    @Test
    @DisplayName("Get appointments test")
    public void getAppointmentsTest() {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();
        Date date = new Date();
        Appointment apt = new Appointment(null, id1, id2, date, 60, "This is the description", "Location");
        Appointment apt2 = new Appointment(null, new ObjectId(), id2, date, 60, "This is the description", "Other location");
        Appointment apt3 = new Appointment(null, id2, id1, date, 60, "This is the description", "Same location");
        apt = repository.insert(apt);
        apt2 = repository.insert(apt2);
        apt3 = repository.insert(apt3);

        List<Appointment> appointments = repository.getAppointments(id1, id2);
        assertThat(appointments.size()).isEqualTo(2);
        assertThat(appointments.get(0).getLocation()).isEqualTo(apt.getLocation());
        assertThat(appointments.get(1).getLocation()).isEqualTo(apt3.getLocation());
    }
}
