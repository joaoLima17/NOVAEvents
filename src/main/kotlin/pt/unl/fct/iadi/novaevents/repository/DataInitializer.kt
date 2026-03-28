package pt.unl.fct.iadi.novaevents.repository

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import java.time.LocalDate

@Component
class DataInitializer(
    private val eventTypeRepository: EventTypeRepository,
    private val clubRepository: ClubRepository,
    private val eventRepository: EventRepository
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        // Idempotent — skip if data already present
        if (clubRepository.count() > 0) return

        // --- Event types ---
        val workshop    = eventTypeRepository.save(EventType(name = "WORKSHOP"))
        val talk        = eventTypeRepository.save(EventType(name = "TALK"))
        val competition = eventTypeRepository.save(EventType(name = "COMPETITION"))
        val social      = eventTypeRepository.save(EventType(name = "SOCIAL"))
        val meeting     = eventTypeRepository.save(EventType(name = "MEETING"))
        val other       = eventTypeRepository.save(EventType(name = "OTHER"))

        // --- Clubs ---
        val chess       = clubRepository.save(
            Club(
                name = "Chess Club",
                description = "A club for chess enthusiasts of all levels.",
                category = ClubCategory.ACADEMIC
            )
        )
        val robotics    = clubRepository.save(Club(name = "Robotics Club",         description = "The Robotics Club is the place to turn ideas into machines. Build, compete, innovate.", category = ClubCategory.TECHNOLOGY))
        val photo       = clubRepository.save(Club(name = "Photography Club",      description = "Capture the world through a lens. From street to studio photography.",                category = ClubCategory.ARTS))
        val hiking      = clubRepository.save(Club(name = "Hiking & Outdoors Club",description = "Explore nature and the great outdoors through organised hikes and camping trips.",     category = ClubCategory.SPORTS))
        val film        = clubRepository.save(Club(name = "Film Society",           description = "A club for film lovers and critics. Screenings, discussions, and filmmaking.",        category = ClubCategory.CULTURAL))

        // --- Events (15 total, at least one per club) ---
        // Chess Club (id=1 logically — DB assigns actual id)
        eventRepository.save(
            Event(
                clubId = chess.id,
                name = "Beginner's Chess Workshop",
                date = LocalDate.of(2026, 3, 10),
                location = "Room A101",
                type = workshop,
                description = "An introductory session for new members."
            )
        )
        eventRepository.save(Event(clubId = chess.id,    name = "Spring Chess Tournament",        date = LocalDate.of(2026, 4,  5), location = "Main Hall",          type = competition, description = "Annual spring tournament open to all members."))
        eventRepository.save(Event(clubId = chess.id,    name = "Advanced Openings Talk",         date = LocalDate.of(2026, 5, 20), location = "Room A101",          type = talk,        description = "Expert analysis of modern opening theory."))

        // Robotics Club
        eventRepository.save(Event(clubId = robotics.id, name = "Arduino Intro Workshop",         date = LocalDate.of(2026, 3, 15), location = "Engineering Lab 2",  type = workshop,    description = "Hands-on introduction to Arduino microcontrollers."))
        eventRepository.save(Event(clubId = robotics.id, name = "RoboCup Preparation Meeting",    date = LocalDate.of(2026, 3, 28), location = "Engineering Lab 1",  type = meeting,     description = "Planning session for the regional RoboCup qualifier."))
        eventRepository.save(Event(clubId = robotics.id, name = "Sensor Integration Talk",        date = LocalDate.of(2026, 4, 22), location = "Auditorium B",       type = talk,        description = "Deep dive into ultrasonic and infrared sensors."))
        eventRepository.save(Event(clubId = robotics.id, name = "Regional Robotics Competition",  date = LocalDate.of(2026, 6,  1), location = "Sports Hall",        type = competition, description = "Competing against teams from other universities."))

        // Photography Club
        eventRepository.save(Event(clubId = photo.id,    name = "Night Photography Workshop",     date = LocalDate.of(2026, 3, 22), location = "Campus Rooftop",     type = workshop,    description = "Techniques for long-exposure and night sky shots."))
        eventRepository.save(Event(clubId = photo.id,    name = "Portrait Photography Talk",      date = LocalDate.of(2026, 4, 14), location = "Arts Studio 3",      type = talk,        description = "Lighting and posing fundamentals for portraits."))
        eventRepository.save(Event(clubId = photo.id,    name = "Photo Walk & Social",            date = LocalDate.of(2026, 5,  9), location = "Main Entrance",      type = social,      description = "A guided photo walk around the campus and surroundings."))

        // Hiking & Outdoors Club
        eventRepository.save(Event(clubId = hiking.id,   name = "Serra da Arrábida Hike",         date = LocalDate.of(2026, 3, 29), location = "Bus Stop Central",   type = other,       description = "Full-day coastal hike with swimming stop."))
        eventRepository.save(Event(clubId = hiking.id,   name = "Trail Safety Workshop",          date = LocalDate.of(2026, 4,  8), location = "Room C205",          type = workshop,    description = "Essential safety skills for mountain trails."))
        eventRepository.save(Event(clubId = hiking.id,   name = "Spring Camping Trip",            date = LocalDate.of(2026, 5, 15), location = "Bus Stop Central",   type = social,      description = "Weekend camping at Parque Natural da Serra de São Mamede."))

        // Film Society
        eventRepository.save(Event(clubId = film.id,     name = "Kubrick Retrospective Screening",date = LocalDate.of(2026, 3, 18), location = "Cinema Room",        type = social,      description = "2001: A Space Odyssey followed by discussion."))
        eventRepository.save(Event(clubId = film.id,     name = "Screenwriting Workshop",         date = LocalDate.of(2026, 4, 30), location = "Arts Studio 1",      type = workshop,    description = "Introduction to three-act structure and scene writing."))
    }
}