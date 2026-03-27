package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import pt.unl.fct.iadi.novaevents.model.EventType

@Repository
interface EventTypeRepository: JpaRepository<EventType, Long> {

    override fun <S : EventType?> save(entity: S & Any): S & Any {
        TODO("Not yet implemented")
    }

}