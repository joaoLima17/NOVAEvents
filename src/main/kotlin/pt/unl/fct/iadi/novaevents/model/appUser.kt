package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class appUser {

    @Id
    var id: Long? = null

    var username: String? = null
    var password: String? = null

}