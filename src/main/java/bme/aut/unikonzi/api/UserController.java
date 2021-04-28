package bme.aut.unikonzi.api;

import bme.aut.unikonzi.exception.UserAlreadyExistsException;
import bme.aut.unikonzi.model.payload.request.LoginUser;
import bme.aut.unikonzi.model.User;
import bme.aut.unikonzi.model.payload.response.JwtResponse;
import bme.aut.unikonzi.security.jwt.JwtUtils;
import bme.aut.unikonzi.security.services.UserDetailsImpl;
import bme.aut.unikonzi.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "api/users", produces = "application/json")
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> addUser(@Valid @NonNull @RequestBody User user) {
        user.setRole(Set.of(User.Role.ROLE_USER));
        user.setPassword(encoder.encode(user.getPassword()));
        try {
            Optional<User> newUser = userService.addUser(user);
            return new ResponseEntity<User>(newUser.get(), HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            String error = "{ \"error\": \"" + e.getMessage() + "\" }";
            return new ResponseEntity<String>(error, HttpStatus.CONFLICT);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers(@RequestParam(defaultValue = "1", required = false) int page,
                                  @RequestParam(defaultValue = "10", required = false) int limit) {
        return userService.getAllUsers(page, limit);
    }

    @GetMapping(path = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable("id") ObjectId id) {
        Optional<User> userMaybe = userService.getUserById(id);
        if (userMaybe.isEmpty()) {
            return new ResponseEntity<String>("{\"error\": \"User with the given id does not exists\"}",
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(userMaybe.get());
    }

    @DeleteMapping(path = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable("id") ObjectId id) {
        if (userService.deleteUser(id) == 0) {
            return new ResponseEntity<String>("{\"error\": \"User with the given id does not exists\"}",
                    HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok("{\"message\": \"The user was deleted successfully\"}");
    }

    @PatchMapping(path = "{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setUserAsAdminById(@PathVariable("id") ObjectId id) {
        if (userService.setUserAsAdmin(id) == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "login")
    public ResponseEntity<?> loginUser(@Valid @NonNull @RequestBody LoginUser loginUser) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(), loginUser.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }
}
