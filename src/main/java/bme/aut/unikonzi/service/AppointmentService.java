package bme.aut.unikonzi.service;

import bme.aut.unikonzi.dao.AppointmentDao;
import bme.aut.unikonzi.model.Appointment;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentDao repository;

    @Autowired
    public AppointmentService(AppointmentDao repository) {
        this.repository = repository;
    }

    public Appointment add(Appointment appointment) {
        return repository.insert(appointment);
    }

    public List<Appointment> getAllBetweenTwoUser(ObjectId id1, ObjectId id2) {
        return repository.getAppointments(id1, id2);
    }
}
