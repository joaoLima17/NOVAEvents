package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.appUser
@Repository
interface AppUserRepository : JpaRepository<appUser, Long> {
    fun findByUsername(username: String): pt.unl.fct.iadi.novaevents.model.appUser
}