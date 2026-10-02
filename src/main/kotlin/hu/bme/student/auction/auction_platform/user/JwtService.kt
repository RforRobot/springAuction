package hu.bme.student.auction.auction_platform.user

import hu.bme.student.auction.auction_platform.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    private val jwtProperties: JwtProperties
) {
    private val signingKey: SecretKey by lazy {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userDetails: UserDetails, extraClaims: Map<String, Any> = emptyMap()): String {
        val now = System.currentTimeMillis()
        return Jwts.builder()
            .claims(extraClaims)
            .subject(userDetails.username)
            .issuedAt(Date(now))
            .expiration(Date(now + jwtProperties.expirationMs))
            .signWith(signingKey)
            .compact()
    }

    fun extractUsername(token: String): String? {
        return extractClaim(token, Claims::getSubject)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        val expiration = extractClaim(token, Claims::getExpiration) ?: return true
        return expiration.before(Date())
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T? {
        val claims = extractAllClaims(token) ?: return null
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (ex: Exception) {
            null
        }
    }
}