package com.ytyo.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ytyo.Model.User;
import com.ytyo.Option.NoneException;
import com.ytyo.Option.Option;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.graalvm.collections.Pair;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET_KEY = "jaisjoajoaief2485m-423mv5m0-ergguw-qie-oiqhd9q812hnd12d1";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static final SecretKey SignSecret = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());


    public static Option<String> encode(String jwtId, User user) {
        try {
            String userJson = mapper.writeValueAsString(user);
            JwtBuilder builder = Jwts.builder()
                    .claim("user", userJson)
                    .setId(jwtId);
            return Option.Some(builder.signWith(SignSecret, SignatureAlgorithm.HS256).compact());
        } catch (JsonProcessingException e) {
            return Option.None();
        }
    }

    public static Option<Pair<String, User>> decode(String jws) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SignSecret)
                    .build()
                    .parseClaimsJws(jws);

            String jwtId = claimsJws.getBody().getId();
            String userJson = claimsJws.getBody().get("user", String.class);
            User user = mapper.readValue(userJson, User.class);

            return Option.Some(Pair.create(jwtId, user));
        } catch (Exception e) {
            return Option.None();
        }
    }

    @Test
    public void test() throws NoneException {
        User user = new User();
        user.setRealName("国服");
        user.setId(1231231L);
        Option<String> jws = encode(UUID.randomUUID().toString(), user);
        Option<Pair<String, User>> pair = decode(jws.unwrap());
        System.out.println(pair.unwrap().getLeft());
        System.out.println(pair.unwrap().getRight());
    }
}
