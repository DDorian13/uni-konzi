package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import bme.aut.unikonzi.service.SubjectService;
import bme.aut.unikonzi.service.UniversityService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "api/universities/{universityId}", produces = "application/json")
@RestController
public class UniSubjectController {

    private final SubjectService subjectService;
    private final UniversityService universityService;

    @Autowired
    public UniSubjectController(SubjectService subjectService, UniversityService universityService) {
        this.subjectService = subjectService;
        this.universityService = universityService;
    }

    @DeleteMapping(path = "{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubjectById(@PathVariable("universityId") ObjectId universityId,
                                               @PathVariable("subjectId") ObjectId subjectId) {
        Optional<University> university = universityService.getUniversityById(universityId, 1, 10);
        Optional<Subject> subject = subjectService.getSubjectById(subjectId);
        if (university.isEmpty()) {
            String error = "{\"error\": \"The id of the university is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        if (subject.isEmpty()) {
            String error = "{\"error\": \"The id of the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }

        universityService.removeSubjectFromUniversity(universityId, subject.get());
        subjectService.deleteSubjectById(subjectId);

        String message = "{\"message\": \"The subject was deleted successfully\"}";
        return ResponseEntity.ok(message);
    }

}
