package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.Comment;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.service.SubjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RequestMapping(value = "api/universities/{universityId}", produces = "application/json")
@RestController
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ResponseEntity<?> addSubject(@PathVariable("universityId") ObjectId universityId,
                                        @Valid @NonNull @RequestBody Subject subject) {
        Optional<Subject> newSubject = subjectService.addSubject(universityId, subject);
        if (newSubject == null) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        } else if (newSubject.isEmpty()) {
            String error = "{\"error\": \"Subject already exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<String>(subjectsToJson(true, newSubject.get()), HttpStatus.CREATED);
    }

    @PatchMapping(path = "{subjectId}")
    public ResponseEntity<?> updateSubjectById(@PathVariable("universityId") ObjectId universityId,
                                               @PathVariable("subjectId") ObjectId subjectId,
                                               @Valid @NonNull @RequestBody Subject subject) {
        Optional<Subject> newSubject = subjectService.updateOrRemoveSubjectById(universityId, subjectId, subject);
        if (newSubject.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(subjectsToJson(true, newSubject.get()));
    }

    @DeleteMapping(path = "{subjectId}")
    public ResponseEntity<?> deleteSubjectById(@PathVariable("universityId") ObjectId universityId,
                                               @PathVariable("subjectId") ObjectId subjectId) {
        Optional<Subject> deletedSubject = subjectService.updateOrRemoveSubjectById(universityId, subjectId, null);
        if (deletedSubject.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        String message = "{\"message\": \"The subject was deleted successfully\"}";
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "{subjectId}")
    public ResponseEntity<?> getSubjectById(@PathVariable("universityId") ObjectId universityId,
                                            @PathVariable("subjectId") ObjectId subjectId) {
        Optional<Subject> subject = subjectService.getSubjectById(universityId, subjectId);
        if (subject.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(subjectsToJson(true, subject.get()));
    }

    @PostMapping(path = "{subjectId}")
    public ResponseEntity<?> addCommentToSubject(@PathVariable("universityId") ObjectId universityId,
                                                 @PathVariable("subjectId") ObjectId subjectId,
                                                 @Valid @NonNull @RequestBody Comment comment) {
        Optional<Comment> newComment = subjectService.addCommentToSubject(universityId, subjectId, comment);
        if (newComment.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Comment>(newComment.get(), HttpStatus.CREATED);
    }

    private String subjectsToJson(boolean withComments, Object value) {
        ObjectMapper mapper = new ObjectMapper();
        FilterProvider filter;
        if (withComments) {
            filter = new SimpleFilterProvider()
                    .addFilter("filterByName", SimpleBeanPropertyFilter.serializeAll());
        } else {
            filter = new SimpleFilterProvider()
                    .addFilter("filterByName", SimpleBeanPropertyFilter.serializeAllExcept("comments"));
        }
        ObjectWriter writer = mapper.writer(filter);
        String result = new String();
        try {
            result = writer.writeValueAsString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
