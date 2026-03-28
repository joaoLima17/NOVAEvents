package pt.unl.fct.iadi.novaevents.service

import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.repository.ClubRepository

@Service
class ClubService(private var clubRepository: ClubRepository) {



    fun findAll(): List<Club> = clubRepository.findAll()

    fun findById(id: Long): Club =
        clubRepository.findById(id).orElseThrow { NoSuchElementException("Club $id not found")}
}