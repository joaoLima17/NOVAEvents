package pt.unl.fct.iadi.novaevents.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RequestAuditInterceptor : HandlerInterceptor {
    private val log = LoggerFactory.getLogger(RequestAuditInterceptor::class.java)

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = if (authentication == null || authentication is AnonymousAuthenticationToken) {
            "anonymous"
        } else {
            authentication.name
        }

        log.info("[{}] {} {} [{}]", principal, request.method, request.requestURI, response.status)
    }
}
