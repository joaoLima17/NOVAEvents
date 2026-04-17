package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class CookieRedirectAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        if (request.method.equals("GET", ignoreCase = true) && request.requestURI != "/login") {
            val target = buildString {
                append(request.requestURI)
                if (!request.queryString.isNullOrBlank()) {
                    append("?")
                    append(request.queryString)
                }
            }
            val cookie = Cookie(LOGIN_REDIRECT_COOKIE_NAME, URLEncoder.encode(target, StandardCharsets.UTF_8))
            cookie.path = "/"
            cookie.isHttpOnly = true
            response.addCookie(cookie)
        }

        response.sendRedirect("/login")
    }

    companion object {
        const val LOGIN_REDIRECT_COOKIE_NAME: String = "login_redirect"
    }
}
