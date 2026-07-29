package com.hawel.identity_service.security;


import com.hawel.identity_service.exception.IdentityApiException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

	@Value("${app.jwt.secret}")
	private String jwtSecret;

	@Value("${app.jwt.access-token-expiration}")
	private long jwtExpirationDates;

	// generate jwt token
	public String generateToken(Authentication authentication) {

		String username = authentication.getName();
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + jwtExpirationDates);

		return Jwts.builder().subject(username).issuedAt(new Date()).expiration(expireDate).signWith(key()).compact();
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	// get username from jwt token
	public String getUsernameFromToken(String token) {
		return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
	}

	// validate JWT token
	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith((SecretKey) key()).build().parse(token);
			return true;
		}
		catch (MalformedJwtException malformedJwtException) {
			throw new IdentityApiException(HttpStatus.BAD_REQUEST, "Invalid JWT token ");
		}
		catch (ExpiredJwtException expiredJwtException) {
			throw new IdentityApiException(HttpStatus.BAD_REQUEST, "Expired JWT token ");
		}
		catch (UnsupportedJwtException unsupportedJwtException) {
			throw new IdentityApiException(HttpStatus.BAD_REQUEST, "Unsupported JWT token ");
		}
		catch (IllegalArgumentException illegalArgumentException) {
			throw new IdentityApiException(HttpStatus.BAD_REQUEST, "Jwt claims string is null or empty");
		}
		catch (Exception e) {
			throw new IdentityApiException(HttpStatus.BAD_REQUEST, "An unexpected error occurred - Invalid JWT token");
		}
	}
}