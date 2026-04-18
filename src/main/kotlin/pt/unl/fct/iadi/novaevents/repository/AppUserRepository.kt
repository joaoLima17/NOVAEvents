package pt.unl.fct.iadi.novaevents.repository

import pt.unl.fct.iadi.novaevents.model.appUser

class AppUserRepository {
    fun findByUsername(username: String): appUser {}
    fun existsByUsername(username: String): Boolean {}
}