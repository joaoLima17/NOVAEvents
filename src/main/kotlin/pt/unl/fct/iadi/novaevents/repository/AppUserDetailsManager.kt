package pt.unl.fct.iadi.novaevents.repository

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Role
import pt.unl.fct.iadi.novaevents.model.appRole
import pt.unl.fct.iadi.novaevents.model.appUser
@Service
class AppUserDetailsManager(private val userRepository: AppUserRepository) : UserDetailsManager {

    override fun loadUserByUsername(username: String): UserDetails {
        val user: appUser = userRepository.findByUsername(username) ?:
        throw UsernameNotFoundException(username)

        return User(user.username, user.password, user.roles.map { SimpleGrantedAuthority(it.role.name)})
    }

    override fun createUser(user: UserDetails) {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: UserDetails?) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(username: String?) {
        TODO("Not yet implemented")
    }

    override fun changePassword(oldPassword: String?, newPassword: String?) {
        TODO("Not yet implemented")
    }

    override fun userExists(username: String): Boolean {
        return userRepository.findByUsername(username) != null
    }
}