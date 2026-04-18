package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtCookieAuthenticationFilter(
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (SecurityContextHolder.getContext().authentication == null) {
            val token = request.cookies
                ?.firstOrNull { it.name == JWT_COOKIE_NAME }
                ?.value

            val principal = token?.let { jwtService.parseToken(it) }
            if (principal != null) {
                val authorities = principal.roles.map { SimpleGrantedAuthority(it) }
                val authentication = UsernamePasswordAuthenticationToken(
                    principal.username,
                    null,
                    authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }

    companion object {
        const val JWT_COOKIE_NAME: String = "jwt"
    }
}
