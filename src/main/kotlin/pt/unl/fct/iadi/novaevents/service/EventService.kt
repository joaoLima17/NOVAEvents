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
    private var eventRepository: EventRepository,
    private var eventTypeRepository: EventTypeRepository
) {
    fun findAllEventTypes(): List<EventType> = eventTypeRepository.findAll()


    fun findEventTypeByName(name: String): EventType =
        eventTypeRepository.findByName(name)
            ?: throw NoSuchElementException("EventType '$name' not found")

    @Transactional
    fun findAll(
        typeName: String? = null,
        clubId: Long? = null,
        from: LocalDate? = null,
        to: LocalDate? = null
    ): List<Event> = eventRepository.findFiltered(typeName, clubId, from, to)

    @Transactional
    fun findById(id: Long): Event =
        eventRepository.findById(id).orElseThrow { NoSuchElementException("Event $id not found") }

    @Transactional
    fun findByClubId(clubId: Long): List<Event> =
        eventRepository.findByClubId(clubId)

    @Transactional
    fun countByClubId(clubId: Long): Long =
        eventRepository.countByClubId(clubId)

    fun existsByName(name: String): Boolean =
        eventRepository.existsByNameIgnoreCase(name)

    fun existsByNameExcluding(name: String, excludeId: Long): Boolean =
        eventRepository.existsByNameIgnoreCaseAndIdNot(name, excludeId)

    @Transactional
    fun create(clubId: Long, form: EventFormDto): Event {
        val eventType = findEventTypeByName(form.type!!)
        val event = Event(
            clubId = clubId,
            name = form.name,
            date = form.date!!,
            location = form.location.ifBlank { null },
            type = eventType,
            description = form.description.ifBlank { null }
        )
        return eventRepository.save(event)
    }

    @Transactional
    fun update(id: Long, form: EventFormDto): Event {
        val event = findById(id)
        val eventType = findEventTypeByName(form.type!!)
        event.name = form.name
        event.date = form.date!!
        event.location = form.location.ifBlank { null }
        event.type = eventType
        event.description = form.description.ifBlank { null }
        return eventRepository.save(event)
    }

    @Transactional
    fun delete(id: Long) {
        if (!eventRepository.existsById(id)) throw NoSuchElementException("Event $id not found")
        eventRepository.deleteById(id)
    }
}