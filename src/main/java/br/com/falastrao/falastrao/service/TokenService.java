package br.com.falastrao.falastrao.service;

import br.com.falastrao.falastrao.model.User;
import br.com.falastrao.falastrao.model.enums.UserRoles;
import br.com.falastrao.falastrao.security.JWTUserData;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.issuer:Falastrao-API}")
    private String issuer;

    public String generateToken(User user) {

        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(user.getEmail())
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole().name())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(expirationTime))
                .sign(algorithm);
    }

    public Optional<JWTUserData> verifyToken(String token){

        try {

            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
                    .verify(token);

            return Optional.of(
                    JWTUserData.builder()
                            .userId(jwt.getClaim("userId").asLong())
                            .email(jwt.getSubject())
                            .role(UserRoles.valueOf(jwt.getClaim("role").asString()))
                            .build()
            );

        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }
}
