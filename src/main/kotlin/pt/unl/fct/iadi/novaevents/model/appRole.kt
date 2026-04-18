package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "role")
class appRole (
    @Id @GeneratedValue
    var id:Long? = 0,
    @Enumerated(EnumType.STRING)
    var role: Role = Role.ROLE_EDITOR,

    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: appUser = appUser()
)

enum class Role {ROLE_EDITOR, ROLE_ADMIN}