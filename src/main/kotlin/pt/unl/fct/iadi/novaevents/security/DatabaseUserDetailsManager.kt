package pt.unl.fct.iadi.novaevents.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.AppUser
import pt.unl.fct.iadi.novaevents.model.RoleName
import pt.unl.fct.iadi.novaevents.model.UserRole
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository

@Service
class DatabaseUserDetailsManager(
    private val appUserRepository: AppUserRepository
) : UserDetailsManager {

    override fun loadUserByUsername(username: String): UserDetails {
        val appUser = appUserRepository.findWithRolesByUsername(username)
            ?: throw UsernameNotFoundException("User '$username' not found")

        val authorities = appUser.roles
            .map { SimpleGrantedAuthority(it.role.name) }

        return User.withUsername(appUser.username)
            .password(appUser.password)
            .authorities(authorities)
            .build()
    }

    override fun createUser(user: UserDetails) {
        if (userExists(user.username)) {
            throw IllegalArgumentException("User '${user.username}' already exists")
        }

        val appUser = AppUser(
            username = user.username,
            password = user.password
        )
        user.authorities.forEach { authority ->
            val role = RoleName.valueOf(authority.authority)
            appUser.roles.add(UserRole(role = role, user = appUser))
        }
        appUserRepository.save(appUser)
    }

    override fun updateUser(user: UserDetails) {
        val existing = appUserRepository.findWithRolesByUsername(user.username)
            ?: throw UsernameNotFoundException("User '${user.username}' not found")

        existing.password = user.password
        existing.roles.clear()
        user.authorities.forEach { authority ->
            val role = RoleName.valueOf(authority.authority)
            existing.roles.add(UserRole(role = role, user = existing))
        }
        appUserRepository.save(existing)
    }

    override fun deleteUser(username: String) {
        val existing = appUserRepository.findByUsername(username) ?: return
        appUserRepository.delete(existing)
    }

    override fun changePassword(oldPassword: String?, newPassword: String?) {
        throw UnsupportedOperationException("Password changes are not supported through this endpoint")
    }

    override fun userExists(username: String): Boolean =
        appUserRepository.existsByUsername(username)
}
