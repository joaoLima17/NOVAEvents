package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "role")
class appRole {
    @Id
    var id:Long = 0
    var role: Role? = null
}

enum class Role {ROLE_EDITOR, ROLE_ADMIN}