package pt.unl.fct.iadi.novaevents.security

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.savedrequest.CookieRequestCache
import pt.unl.fct.iadi.novaevents.repository.AppUserDetailsManager
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository
import java.awt.PageAttributes


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(private val appUserRepository: AppUserRepository, private val jwtCookieAuthFilter: JwtCookieAuthFilter, private val JwtAuthSuccessHandler: JwtAuthSuccessHandler) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
    @Bean
    fun userDetailsManager(): UserDetailsManager = AppUserDetailsManager(appUserRepository)

    @Bean
    @Order(1)
    fun internalApiSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("/api/**")
            .csrf { it.disable() }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .exceptionHandling {
                it.authenticationEntryPoint { _, res, _ ->
                    res.status = HttpServletResponse.SC_UNAUTHORIZED
                    res.contentType = MediaType.APPLICATION_JSON_VALUE
                    res.writer.write("""{"error":"Unauthorized"}""")
                }
                it.accessDeniedHandler { _, res, _ ->
                    res.status = HttpServletResponse.SC_FORBIDDEN
                    res.contentType = MediaType.APPLICATION_JSON_VALUE
                    res.writer.write("""{"error":"Forbidden"}""")
                }
            }
        return http.build()
    }

    @Bean
    @Order(2)
    fun webSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .securityContext { it.securityContextRepository(RequestAttributeSecurityContextRepository()) }
            .requestCache { it.requestCache(CookieRequestCache()) }
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository())
                csrf.csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
            }
            .addFilterBefore(jwtCookieAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .logout { it.deleteCookies("jwt") }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.GET, "/clubs/**", "/events/**", "/", "/login").permitAll()
                    .requestMatchers("/clubs/*/events/new", "/clubs/*/events/*/edit").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/clubs/*/events/*").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login").permitAll()
                it.successHandler(JwtAuthSuccessHandler)
            }
        return http.build()
    }
}