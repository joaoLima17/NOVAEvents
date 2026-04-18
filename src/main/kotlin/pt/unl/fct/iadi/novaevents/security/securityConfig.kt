package pt.unl.fct.iadi.novaevents.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import pt.unl.fct.iadi.novaevents.repository.AppUserDetailsManager
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository


@Configuration
@EnableWebSecurity
class SecurityConfig(private val appUserRepository: AppUserRepository) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
    @Bean
    fun userDetailsManager(): UserDetailsManager = AppUserDetailsManager(appUserRepository)
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http

            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.GET, "/posts", "/posts/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/posts").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/posts/**").hasAnyRole("EDITOR", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/posts/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .httpBasic {}
        return http.build()
    }
}