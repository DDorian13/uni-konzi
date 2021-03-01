package bme.aut.unikonzi.api;

import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/v1/user")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void addUser(@Valid @NonNull @RequestBody User user) {
        userService.addUser(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(path = "{id}")
    public User getUserById(@PathVariable("id") ObjectId id) {
        return userService.getUserById(id)
                .orElse(null);
    }

    @DeleteMapping(path = "{id}")
    public void deleteUserById(@PathVariable("id") ObjectId id) {
        userService.deleteUser(id);
    }

    @PutMapping(path = "{id}")
    public void updateUserById(@PathVariable("id") ObjectId id, @Valid @NonNull @RequestBody User userToUpdate){
        userService.updateUser(id, userToUpdate);
    }

    @RequestMapping(method = RequestMethod.HEAD, path = "{id}")
    public void setUserAsAdminById(@PathVariable("id") ObjectId id) {
        userService.setUserAsAdmin(id);
    }
}
