package pt.unl.fct.iadi.novaevents.security

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.service.EventService

@Service("eventOwnershipService")
class EventOwnershipService(
    private val eventService: EventService
) {
    fun isOwner(eventId: Long, username: String): Boolean =
        eventService.isOwner(eventId, username)
}
