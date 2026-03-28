package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pt.unl.fct.iadi.novaevents.model.EventType

@Repository
interface EventTypeRepository: JpaRepository<EventType, Long> {

    fun findByName(name: String): EventType?

}