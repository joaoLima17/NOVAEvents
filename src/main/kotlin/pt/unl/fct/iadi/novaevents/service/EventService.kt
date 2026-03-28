package pt.unl.fct.iadi.novaevents.service


import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository
import java.time.LocalDate

@Service
class EventService(
    private val eventRepository: EventRepository,
    private val eventTypeRepository: EventTypeRepository
) {
    fun findAllEventTypes(): List<EventType> = eventTypeRepository.findAll()


    fun findEventTypeByName(name: String): EventType =
        eventTypeRepository.findByName(name)
            ?: throw NoSuchElementException("EventType '$name' not found")

    @Transactional(readOnly = true)
    fun findAll(
        type: EventType? = null,
        clubId: Long? = null,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): List<Event> = events.filter { event ->
        (type == null || event.type == type) &&
                (clubId == null || event.clubId == clubId) &&
                (from == null || !event.date.isBefore(from)) &&
                (to == null || !event.date.isAfter(to))
    }

    fun findById(id: Long): Event =
        events.find { it.id == id } ?: throw NoSuchElementException("Event $id not found")

    fun findByClubId(clubId: Long): List<Event> =
        events.filter { it.clubId == clubId }

    fun existsByName(name: String): Boolean =
        events.any { it.name.equals(name, ignoreCase = true) }

    fun existsByNameExcluding(name: String, excludeId: Long): Boolean =
        events.any { it.name.equals(name, ignoreCase = true) && it.id != excludeId }

    fun create(clubId: Long, form: EventFormDto): Event {
        val event = Event(
            id = nextId++,
            clubId = clubId,
            name = form.name,
            date = form.date!!,
            location = form.location.ifBlank { null },
            type = form.type!!,
            description = form.description.ifBlank { null }
        )
        events.add(event)
        return event
    }

    fun update(id: Long, form: EventFormDto): Event {
        val index = events.indexOfFirst { it.id == id }
        if (index == -1) throw NoSuchElementException("Event $id not found")
        val updated = events[index].copy(
            name = form.name,
            date = form.date!!,
            location = form.location.ifBlank { null },
            type = form.type!!,
            description = form.description.ifBlank { null }
        )
        events[index] = updated
        return updated
    }

    fun delete(id: Long) {
        val removed = events.removeIf { it.id == id }
        if (!removed) throw NoSuchElementException("Event $id not found")
    }
}