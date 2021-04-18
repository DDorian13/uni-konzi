package bme.aut.unikonzi.dao.impl;

import bme.aut.unikonzi.dao.AppointmentDao;
import bme.aut.unikonzi.model.Appointment;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MongoAppointmentDao implements AppointmentDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static String collectionName = "appointments";

    @Override
    public Appointment insert(Appointment appointment) {
        return mongoTemplate.insert(appointment);
    }

    @Override
    public List<Appointment> getAppointments(ObjectId id1, ObjectId id2) {
        Query query = new Query();
        query.addCriteria(Criteria.where("creatorId").in(id1, id2).and("participantId").in(id1, id2));
        return mongoTemplate.find(query, Appointment.class, collectionName);
    }
}
