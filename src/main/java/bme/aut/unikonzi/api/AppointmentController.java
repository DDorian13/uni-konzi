package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.Appointment;
import bme.aut.unikonzi.service.AppointmentService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "api/appointments", produces = "application/json")
@RestController
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ResponseEntity<?> newAppointment(@Valid @NonNull @RequestBody Appointment appointment) {
        return ResponseEntity.ok(appointmentService.add(appointment));
    };

    @GetMapping(path = "{id1}/{id2}")
    public ResponseEntity<?> getAppointmentsBetweenUsers(@PathVariable("id1") ObjectId id1,
                                                         @PathVariable("id2") ObjectId id2) {
        return ResponseEntity.ok(appointmentService.getAllBetweenTwoUser(id1, id2));
    }
}
