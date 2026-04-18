package pt.unl.fct.iadi.novaevents.repository

import org.springframework.stereotype.Repository
import pt.unl.fct.iadi.novaevents.model.appUser
@Repository
interface AppUserRepository {
    fun findByUsername(username: String): appUser

}