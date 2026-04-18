package pt.unl.fct.iadi.novaevents.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(private val appUserRepository: AppUserRepository, private val jwtCookieAuthFilter: JwtCookieAuthFilter, private val JwtAuthSuccessHandler: JwtAuthSuccessHandler) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
    @Bean
    fun userDetailsManager(): UserDetailsManager = AppUserDetailsManager(appUserRepository)
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .logout { logout ->
                logout.deleteCookies("jwt")
            }
            .securityContext {
                it.securityContextRepository(RequestAttributeSecurityContextRepository())
            }
            .requestCache { it.requestCache(CookieRequestCache()) }
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository())
                csrf.csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler())
            }
            .addFilterBefore(
                jwtCookieAuthFilter,
                UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/", "/clubs", "/events/**").permitAll()

                    .requestMatchers("/events/new", "/events/*/edit")
                    .hasAnyRole("EDITOR", "ADMIN")

                    .requestMatchers("/events/*/delete")
                    .hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login").permitAll()
                it.successHandler(JwtAuthSuccessHandler)
            }
        return http.build()
    }
}