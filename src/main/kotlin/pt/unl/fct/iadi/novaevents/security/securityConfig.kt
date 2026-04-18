package pt.unl.fct.iadi.novaevents.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.UserDetailsManager
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository


@Configuration
@EnableWebSecurity
class SecurityConfig(private val appUserRepository: AppUserRepository) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
    @Bean
    fun userDetailsManager(): UserDetailsManager = AppUserDetailsManager(appUserRepository)
}