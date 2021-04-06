package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.Comment;
import bme.aut.unikonzi.model.Subject;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.model.payload.request.CommentBody;
import bme.aut.unikonzi.security.jwt.JwtUtils;
import bme.aut.unikonzi.service.SubjectService;
import bme.aut.unikonzi.service.UserService;
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
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "api/subjects", produces = "application/json")
@RestController
public class SubjectController {

    private final SubjectService subjectService;
    private final UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    public SubjectController(SubjectService subjectService, UserService userService) {
        this.subjectService = subjectService;
        this.userService = userService;
    }
    
    @PatchMapping(path = "{subjectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSubjectById(@PathVariable("subjectId") ObjectId subjectId,
                                               @Valid @NonNull @RequestBody Subject subject) {
        Optional<Subject> newSubject = subjectService.updateSubjectById(subjectId, subject);
        if (newSubject.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(subjectsToJson(new String[]{"tutors", "pupils"}, newSubject.get()));
    }

    @GetMapping(path = "{subjectId}/comments")
    public ResponseEntity<?> getSubjectByIdWithComments(@PathVariable("subjectId") ObjectId subjectId) {
        Optional<Subject> subject = subjectService.getSubjectById(subjectId);
        if (subject.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(subjectsToJson(new String[]{"tutors", "pupils"}, subject.get()));
    }

    @PostMapping(path = "{subjectId}/comments")
    public ResponseEntity<?> addCommentToSubject(@PathVariable("subjectId") ObjectId subjectId,
                                                 @Valid @NonNull @RequestBody CommentBody comment,
                                                 @RequestHeader("Authorization") String token) {
        User user = userService.getUserByName(jwtUtils.getUserNameFromJwtToken(
                token.substring(7, token.length())
        )).get();
        Optional<Comment> newComment = subjectService.addCommentToSubject(subjectId, new Comment(null, user, comment.getText()));
        if (newComment.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Comment>(newComment.get(), HttpStatus.CREATED);
    }

    @GetMapping(path = "{subjectId}/tutors")
    public ResponseEntity<?> getSubjectByIdWithTutors(@PathVariable("subjectId") ObjectId subjectId) {
        return getSubjectByIdExcept(subjectId, new String[]{"comments", "pupils"});
    }

    @PostMapping(path = "{subjectId}/tutors")
    public ResponseEntity<?> addTutorToSubject(@PathVariable("subjectId") ObjectId subjectId,
                                               @RequestHeader("Authorization") String token) {
        User user = userService.getUserByName(jwtUtils.getUserNameFromJwtToken(
                token.substring(7, token.length())
        )).get();
        boolean successful = subjectService.addTutorOrPupilToSubject(subjectId, user, "tutor");
        if (!successful) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        String message = "{\"message\": \"Tutor added\"}";
        return ResponseEntity.ok(message);
    }

    @GetMapping(path = "{subjectId}/pupils")
    public ResponseEntity<?> getSubjectByIdWithPupils(@PathVariable("subjectId") ObjectId subjectId) {
        return getSubjectByIdExcept(subjectId, new String[]{"comments", "tutors"});
    }

    @PostMapping(path = "{subjectId}/pupils")
    public ResponseEntity<?> addPupilToSubject(@PathVariable("subjectId") ObjectId subjectId,
                                               @RequestHeader("Authorization") String token) {
        User user = userService.getUserByName(jwtUtils.getUserNameFromJwtToken(
                token.substring(7, token.length())
        )).get();
        boolean successful = subjectService.addTutorOrPupilToSubject(subjectId, user, "pupil");
        if (!successful) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        String message = "{\"message\": \"Pupil added\"}";
        return ResponseEntity.ok(message);
    }

    @DeleteMapping(path = "{subjectId}/pupils/{userId}")
    public ResponseEntity<?> removeUserFromSubjectPupil(@PathVariable("subjectId") ObjectId subjectId,
                                                        @PathVariable("userId") ObjectId userId) {
        Optional<User> userMaybe = userService.getUserById(userId);
        if (userMaybe.isEmpty()) {
            String error = "{\"error\": \"The id of the user is invalid\"}";
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        boolean success = subjectService.removeUserFromSubjectPupils(subjectId, userMaybe.get());
        if (!success) {
            String error = "{\"error\": \"The id of the subject is invalid or user was not a pupil\"}";
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("{\"message\": \"Pupil removed\"}");
    }

    @GetMapping(value = "tutor-of")
    public ResponseEntity<?> subjectsTutorBy(@RequestHeader("Authorization") String token,
                                             @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                             @RequestParam(name = "limit", defaultValue = "10", required = false) int limit){
        User user = userService.getUserByName(jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()))).get();
        return ResponseEntity.ok(subjectsToJson(new String[] {"comments", "pupils", "tutors"},
                subjectService.tutorOrPupilOf("tutors", user, page, limit)));
    }

    @GetMapping(value = "pupil-of")
    public ResponseEntity<?> subjectsPupilBy(@RequestHeader("Authorization") String token,
                                             @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                             @RequestParam(name = "limit", defaultValue = "10", required = false) int limit){
        User user = userService.getUserByName(jwtUtils.getUserNameFromJwtToken(token.substring(7, token.length()))).get();
        return ResponseEntity.ok(subjectsToJson(new String[] {"comments", "pupils", "tutors"},
                subjectService.tutorOrPupilOf("pupils", user, page, limit)));
    }

    @GetMapping("search")
    public ResponseEntity<?> getSubjectsByName(@RequestParam(name = "nameLike") String name,
                                               @RequestParam(name = "page", defaultValue = "1", required = false) int page,
                                               @RequestParam(name = "limit", defaultValue = "10", required = false) int limit) {
        return ResponseEntity.ok(subjectsToJson(new String[] {"tutors", "comments", "pupils"},
                subjectService.getSubjectsByName(name, page, limit)));
    }

    private ResponseEntity<?> getSubjectByIdExcept(ObjectId subjectId, String[] exceptions) {
        Optional<Subject> subject = subjectService.getSubjectById(subjectId);
        if (subject.isEmpty()) {
            String error = "{\"error\": \"The id of the university or the subject is invalid\"}";
            return new ResponseEntity<String>(error, HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(subjectsToJson(exceptions, subject.get()));
    }


    public static String subjectsToJson(String[] exceptions, Object value) {
        ObjectMapper mapper = new ObjectMapper();
        FilterProvider filter;
        filter = new SimpleFilterProvider()
                .addFilter("filterByName", SimpleBeanPropertyFilter.serializeAllExcept(exceptions));
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
