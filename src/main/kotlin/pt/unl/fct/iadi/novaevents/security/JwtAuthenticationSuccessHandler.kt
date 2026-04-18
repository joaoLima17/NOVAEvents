package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Duration

@Component
class JwtAuthenticationSuccessHandler(
    private val jwtService: JwtService
) : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val token = jwtService.createToken(authentication.name, authentication.authorities)
        val jwtCookie = ResponseCookie.from(JwtCookieAuthenticationFilter.JWT_COOKIE_NAME, token)
            .httpOnly(true)
            .secure(request.isSecure)
            .path("/")
            .sameSite("Lax")
            .maxAge(Duration.ofHours(8))
            .build()
        response.addHeader("Set-Cookie", jwtCookie.toString())

        val redirectCookie = request.cookies
            ?.firstOrNull { it.name == CookieRedirectAuthenticationEntryPoint.LOGIN_REDIRECT_COOKIE_NAME }

        val redirectTarget = redirectCookie
            ?.value
            ?.let { URLDecoder.decode(it, StandardCharsets.UTF_8) }
            ?.takeIf { it.startsWith("/") }
            ?: "/clubs"

        val clearRedirectCookie = Cookie(CookieRedirectAuthenticationEntryPoint.LOGIN_REDIRECT_COOKIE_NAME, "")
        clearRedirectCookie.path = "/"
        clearRedirectCookie.maxAge = 0
        clearRedirectCookie.isHttpOnly = true
        response.addCookie(clearRedirectCookie)

        response.sendRedirect(redirectTarget)
    }
}
