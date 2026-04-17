package pt.unl.fct.iadi.novaevents.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.savedrequest.NullRequestCache

@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtCookieAuthenticationFilter: JwtCookieAuthenticationFilter,
    private val jwtAuthenticationSuccessHandler: JwtAuthenticationSuccessHandler,
    private val jwtLogoutSuccessHandler: JwtLogoutSuccessHandler,
    private val cookieRedirectAuthenticationEntryPoint: CookieRedirectAuthenticationEntryPoint
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            }
            .sessionManagement { sessions ->
                sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .requestCache { cache ->
                cache.requestCache(NullRequestCache())
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(cookieRedirectAuthenticationEntryPoint)
            }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(HttpMethod.GET, "/clubs", "/clubs/*", "/events", "/clubs/*/events/*").permitAll()
                auth.requestMatchers(HttpMethod.GET, "/login", "/error").permitAll()

                auth.requestMatchers(HttpMethod.GET, "/clubs/*/events/new", "/clubs/*/events/*/edit", "/clubs/*/events/*/delete")
                    .hasAnyRole("EDITOR", "ADMIN")
                auth.requestMatchers(HttpMethod.POST, "/clubs/*/events")
                    .hasAnyRole("EDITOR", "ADMIN")
                auth.requestMatchers(HttpMethod.PUT, "/clubs/*/events/*")
                    .hasAnyRole("EDITOR", "ADMIN")
                auth.requestMatchers(HttpMethod.DELETE, "/clubs/*/events/*")
                    .hasAnyRole("EDITOR", "ADMIN")

                auth.anyRequest().authenticated()
            }
            .formLogin { form ->
                form.loginPage("/login")
                    .successHandler(jwtAuthenticationSuccessHandler)
                    .failureUrl("/login?error")
                    .permitAll()
            }
            .logout { logout ->
                logout.logoutUrl("/logout")
                    .logoutSuccessHandler(jwtLogoutSuccessHandler)
            }
            .addFilterBefore(jwtCookieAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
