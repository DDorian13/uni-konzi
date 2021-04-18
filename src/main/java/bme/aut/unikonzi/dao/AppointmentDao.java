package bme.aut.unikonzi.dao;

import bme.aut.unikonzi.model.Appointment;
import org.bson.types.ObjectId;

import java.util.List;

public interface AppointmentDao {

    Appointment insert(Appointment appointment);

    List<Appointment> getAppointments(ObjectId id1, ObjectId id2);
}
