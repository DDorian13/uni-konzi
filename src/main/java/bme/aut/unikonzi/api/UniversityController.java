package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.University;
import bme.aut.unikonzi.service.SubjectService;
import bme.aut.unikonzi.service.UniversityService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static bme.aut.unikonzi.api.SubjectController.subjectsToJson;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "api/universities", produces = "application/json")
@RestController
public class UniversityController {

    private final UniversityService universityService;
    private final SubjectService subjectService;

    @Autowired
    public UniversityController(UniversityService universityService, SubjectService subjectService) {
        this.universityService = universityService;
        this.subjectService = subjectService;
    }

    @GetMapping
    public ResponseEntity<?> getAllUniversities(@RequestParam(defaultValue = "1", required = false) int page,
                                               @RequestParam(defaultValue = "10", required = false) int limit) {
        String result = universitiesToJson(false, universityService.getAllUniversities(page, limit));
        return ResponseEntity.ok(result);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUniversity(@Valid @NonNull @RequestBody University university) {
        Optional<University> newUniversity = universityService.addUniversity(university);
        if (newUniversity.isEmpty()) {
            String error = "{\"error\": \"University already exists\"}";
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(universitiesToJson(false, newUniversity.get()), HttpStatus.CREATED);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getUniversityById(@PathVariable("id") ObjectId id,
                                               @RequestParam(defaultValue = "1", required = false) int page,
                                               @RequestParam(defaultValue = "10", required = false) int limit) {
        Optional<University> university = universityService.getUniversityById(id, page, limit);
        if (university.isEmpty()) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(universitiesToJson(true, university.get()));
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUniversityById(@PathVariable("id") ObjectId id) {
        if (universityService.deleteUniversity(id) == 0) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        String message = "{\"message\": \"The university was deleted successfully\"}";
        return ResponseEntity.ok(message);
    }

    @PatchMapping(path = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUniversityById(@PathVariable("id") ObjectId id,
                                                  @Valid @NonNull @RequestBody University university) {
        Optional<University> updatedUniversity = universityService.updateUniversityById(id, university);
        if (updatedUniversity.isEmpty()) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(universitiesToJson(true, updatedUniversity.get()));
    }

    @GetMapping(path = "search")
    public ResponseEntity<?> getUniversitiesByNameLike(@RequestParam("nameLike") String nameLike,
                                                      @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                                      @RequestParam(name = "limit", defaultValue = "10", required = false) int limit) {
        return ResponseEntity.ok(
                universitiesToJson(false,
                        universityService.getUniversitiesByNameRegex(nameLike, page, limit)));
    }

    @PostMapping(path = "{universityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addSubject(@PathVariable("universityId") ObjectId universityId,
                                        @Valid @NonNull @RequestBody Subject subject) {
        Optional<University> uniMaybe = universityService.getUniversityById(universityId, 1, 10);
        if (uniMaybe.isEmpty()) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        University uni = uniMaybe.get();
        List<Subject> subjects = uni.getSubjects();
        for (Subject _subject : subjects) {
            if (_subject.getCode().equalsIgnoreCase(subject.getCode())) {
                String error = "{\"error\": \"Subject already exists\"}";
                return new ResponseEntity<String>(error, HttpStatus.CONFLICT);
            }
        }
        Subject newSubject = subjectService.addSubject(subject).get();
        uni.addSubject(newSubject);
        universityService.updateUniversityById(universityId, uni);
        return new ResponseEntity<String>(subjectsToJson(new String[]{"tutors", "pupils"}, newSubject), HttpStatus.CREATED);
    }


    private String universitiesToJson(boolean withSubjects, Object value) {
        ObjectMapper mapper = new ObjectMapper();
        FilterProvider filter;
        if (withSubjects) {
            filter = new SimpleFilterProvider()
                    .addFilter("filterByName", SimpleBeanPropertyFilter.serializeAllExcept("comments", "tutors", "pupils"));
        } else {
            filter = new SimpleFilterProvider()
                    .addFilter("filterByName", SimpleBeanPropertyFilter.serializeAllExcept("subjects"));
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
