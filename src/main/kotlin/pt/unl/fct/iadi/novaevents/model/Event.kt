package pt.unl.fct.iadi.novaevents.model;

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "events")
class Event (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var clubId: Long,
    var name: String,
    var date: LocalDate,
    var location: String? = null,
    var type: EventType,
    var description: String? = null,
)

