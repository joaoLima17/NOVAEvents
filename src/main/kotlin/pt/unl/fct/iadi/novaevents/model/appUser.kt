package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "app_users")
class appUser (

    @Id @GeneratedValue
    var id: Long = 0,
    @Column(unique = true)
    var username: String = "",
    var password: String = "",

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    val roles: MutableList<appRole> = mutableListOf()
)