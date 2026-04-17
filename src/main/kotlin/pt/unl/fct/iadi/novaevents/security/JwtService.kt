package pt.unl.fct.iadi.novaevents.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

data class JwtPrincipal(
    val username: String,
    val roles: List<String>
)

@Service
class JwtService(
    @Value("\${app.security.jwt.secret}") secret: String,
    @Value("\${app.security.jwt.ttl-seconds:28800}") private val ttlSeconds: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun createToken(username: String, authorities: Collection<GrantedAuthority>): String {
        val now = Instant.now()
        val roles = authorities.map { it.authority }

        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(ttlSeconds)))
            .signWith(key)
            .compact()
    }

    fun parseToken(token: String): JwtPrincipal? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            val username = claims.subject ?: return null
            val roles = (claims["roles"] as? Collection<*>)
                ?.mapNotNull { it?.toString() }
                ?: emptyList()

            JwtPrincipal(username, roles)
        } catch (_: JwtException) {
            null
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
