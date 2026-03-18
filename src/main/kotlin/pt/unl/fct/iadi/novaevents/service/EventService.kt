package pt.unl.fct.iadi.novaevents.service


import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import java.time.LocalDate

@Service
class EventService {
    private var nextId = 1L
    private val events = mutableListOf(
        Event(nextId++, 1, "Beginner's Chess Workshop", LocalDate.of(2026, 3, 10), "Room A101", EventType.WORKSHOP, "Annual opening tournament."),
        Event(nextId++, 2, "Spring Chess Tournament", LocalDate.of(2026, 4, 5), "Main Hall", EventType.COMPETITION, "Hands-on Arduino session."),
        Event(nextId++, 3, "Advanced Openings Talk", LocalDate.of(2026, 5, 20), "Room A101", EventType.TALK, "A guided photo walk."),
        Event(nextId++, 4, "Arduino Intro Workshop", LocalDate.of(2026, 3, 15), "Engineering Lab 2", EventType.WORKSHOP, "Full day hike."),
        Event(nextId++, 5, "RoboCup Preparation Meeting", LocalDate.of(2026, 3, 28), "Engineering Lab 1", EventType.MEETING, "Screening and discussion."),
        Event(nextId++, 6, "Sensor Integration Talk", LocalDate.of(2026, 4, 22), "Auditorium B", EventType.TALK, "Annual opening tournament."),
        Event(nextId++, 7, "Regional Robotics Competition", LocalDate.of(2026, 6, 1), "Sports Hall", EventType.COMPETITION, "Hands-on Arduino session."),
        Event(nextId++, 8, "Night Photography Workshop", LocalDate.of(2026, 3, 22), "Campus Rooftop", EventType.WORKSHOP, "A guided photo walk."),
        Event(nextId++, 9, "Portrait Photography Talk", LocalDate.of(2026, 4, 14), "Arts Studio 3", EventType.TALK, "Full day hike."),
        Event(nextId++, 10, "Photo Walk & Social", LocalDate.of(2026, 5, 9), "Main Entrance", EventType.SOCIAL, "Screening and discussion."),
        Event(nextId++, 11, "Serra da Arrábida Hike", LocalDate.of(2026, 3, 29), "Bus Stop Central", EventType.OTHER, "Annual opening tournament."),
        Event(nextId++, 12, "Trail Safety Workshop", LocalDate.of(2026, 4, 8), "Room C205", EventType.WORKSHOP, "Hands-on Arduino session."),
        Event(nextId++, 13, "Spring Camping Trip", LocalDate.of(2026, 5, 15), "Bus Stop Central", EventType.SOCIAL, "A guided photo walk."),
        Event(nextId++, 14, "Kubrick Retrospective Screening", LocalDate.of(2026, 3, 18), "Cinema Room", EventType.SOCIAL, "Full day hike."),
        Event(nextId++, 15, "Screenwriting Workshop", LocalDate.of(2026, 4, 30), "Arts Studio 1", EventType.WORKSHOP, "Screening and discussion.")
    )
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