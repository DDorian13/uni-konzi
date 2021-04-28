package bme.aut.unikonzi.helper;

import bme.aut.unikonzi.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.Set;

public class TokenMock {

    private static final String jwtSecret = "myOnlySecretOfOnlabJavaSpringProject";

    private static final int jwtExpirationMs = 60000;

    public static User admin = new User(new ObjectId(), "username", "my@email.com", "password",
            Set.of(User.Role.ROLE_USER, User.Role.ROLE_ADMIN));

    public static User user = new User(new ObjectId(), "username", "my@email.com", "password",
            Set.of(User.Role.ROLE_USER));

    public static String getAdminToken(String username) {
        return "Bearer " + Jwts.builder()
                .setSubject(username)
                .claim("roles", new String[]{"ROLE_USER", "ROLE_ADMIN"})
                .claim("userId", new ObjectId())
                .claim("email", "my@email.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public static String getUserToken(String username) {
        return "Bearer " + Jwts.builder()
                .setSubject(username)
                .claim("roles", new String[]{"ROLE_USER"})
                .claim("userId", new ObjectId())
                .claim("email", "my@email.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
