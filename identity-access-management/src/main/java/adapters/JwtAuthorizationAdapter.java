package adapters;

import app.AuthorizationPort;
import app.Token;
import domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
class JwtAuthorizationAdapter implements AuthorizationPort {

  private final String secretKey;
  private final long expirationMs;

  public JwtAuthorizationAdapter(@Value("${jwt.secret}") String secretKey,
      @Value("${jwt.expiration.ms}") long expirationMs) {
    this.secretKey = secretKey;
    this.expirationMs = expirationMs;
  }

  @Override
  public Token generateToken(final User user) {
    long now = System.currentTimeMillis();
    Date validity = new Date(now + expirationMs);
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));

    String subject = user.getId().toString();

    Map<String, Object> claims = Map.of(
        "name", user.getName()
    );

    String tokenValue = Jwts.builder()
        .claims(claims)
        .subject(subject)
        .issuedAt(new Date(now))
        .expiration(validity)
        .signWith(key)
        .compact();

    return new Token(tokenValue);
  }
}
