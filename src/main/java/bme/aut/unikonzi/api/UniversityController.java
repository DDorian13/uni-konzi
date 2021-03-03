package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.University;
import bme.aut.unikonzi.service.UniversityService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "api/universities", produces = "application/json")
@RestController
public class UniversityController {

    private final UniversityService universityService;

    @Autowired
    public UniversityController(UniversityService universityService) {
        this.universityService = universityService;
    }

    @GetMapping
    public List<University> getAllUniversities(@RequestParam(defaultValue = "1", required = false) int page,
                                               @RequestParam(defaultValue = "10", required = false) int limit) {
        return universityService.getAllUniversities(page, limit);
    }

    @PostMapping
    public ResponseEntity<?> addUniversity(@Valid @NonNull @RequestBody University university) {
        Optional<University> newUniversity = universityService.addUniversity(university);
        if (newUniversity.isEmpty()) {
            String error = "{\"error\": \"University already exists\"}";
            return new ResponseEntity<>(error, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(newUniversity.get(), HttpStatus.CREATED);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getUniversityById(@PathVariable("id") ObjectId id) {
        Optional<University> university = universityService.getUniversityById(id);
        if (university.isEmpty()) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(university.get());
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteUniversityById(@PathVariable("id") ObjectId id) {
        if (universityService.deleteUniversity(id) == 0) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        String message = "{\"message\": \"The university was deleted successfully\"}";
        return ResponseEntity.ok(message);
    }

    @PatchMapping(path = "{id}")
    public ResponseEntity<?> updateUniversityById(@PathVariable("id") ObjectId id,
                                                  @Valid @NonNull @RequestBody University university) {
        Optional<University> updatedUniversity = universityService.updateUniversityById(id, university);
        if (updatedUniversity.isEmpty()) {
            String error = "{\"error\": \"University with the given id does not exists\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updatedUniversity.get());
    }

    @GetMapping(path = "search")
    public List<University> getUniversitiesByNameLike(@RequestParam("nameLike") String nameLike,
                                                      @RequestParam(defaultValue = "1", required = false) int page,
                                                      @RequestParam(defaultValue = "10", required = false) int limit) {
        return universityService.getUniversitiesByNameRegex(nameLike, page, limit);
    }
}
