package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory

@Service
class ClubService {

    private val clubs = mutableListOf(
        Club(1, "Chess Club", "A club for chess enthusiasts.", ClubCategory.ACADEMIC),
        Club(2, "Robotics Club", "The Robotics Club is the place to turn ideas into machines.", ClubCategory.TECHNOLOGY),
        Club(3, "Photography Club", "Capture the world through a lens.", ClubCategory.ARTS),
        Club(4, "Hiking & Outdoors Club", "Explore nature and the great outdoors.", ClubCategory.SPORTS),
        Club(5, "Film Society", "A club for film lovers and critics.", ClubCategory.CULTURAL)
    )

    fun findAll(): List<Club> = clubs

    fun findById(id: Long): Club =
        clubs.find { it.id == id } ?: throw NoSuchElementException("Club $id not found")
}