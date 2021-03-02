package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.service.UserService;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(value = "api/v1/user", produces = "application/json")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addUser(@Valid @NonNull @RequestBody User user) {
        User newUser = userService.addUser(user);
        if (newUser == null) {
            String text = "{\"error\": \"Already registered with this email\"}";
            return new ResponseEntity<String>(text, HttpStatus.CONFLICT);
        }
        return ResponseEntity.ok(newUser);
    }

    @GetMapping
    public List<User> getAllUsers(@RequestParam(defaultValue = "1", required = false) int page,
                                  @RequestParam(defaultValue = "5", required = false) int limit) {
        return userService.getAllUsers(page, limit);
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") ObjectId id) {
        var userMaybe = userService.getUserById(id);
        if (userMaybe.isEmpty()) {
            return new ResponseEntity<String>("{\"error\": \"User with the given id does not exists\"}",
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userMaybe.get());
    }

    @DeleteMapping(path = "{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") ObjectId id) {
        if (userService.deleteUser(id) == 0) {
            return new ResponseEntity<String>("{\"error\": \"User with the given id does not exists\"}",
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("{\"message\": \"The user was deleted successfully\"}");
    }

    @RequestMapping(method = RequestMethod.HEAD, path = "{id}")
    public ResponseEntity<?> setUserAsAdminById(@PathVariable("id") ObjectId id) {
        if (userService.setUserAsAdmin(id) == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
