package pt.unl.fct.iadi.novaevents.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.EventService
import java.time.LocalDate

@Controller
@RequestMapping("/events")
class GlobalEventsController(
    private val eventService: EventService,
    private val clubService: ClubService
) {

    @GetMapping
    fun listAllEvents(
        @RequestParam(required = false) type: String?,
        @RequestParam(required = false) clubId: Long?,
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?,
        model: ModelMap
    ): String {
        val clubs = clubService.findAll()
        model.addAttribute("events", eventService.findAll(type, clubId, from, to))
        model.addAttribute("clubs", clubs)
        model.addAttribute("eventTypes", eventService.findAllEventTypes())
        model.addAttribute("selectedType", type)
        model.addAttribute("selectedClubId", clubId)
        model.addAttribute("from", from)
        model.addAttribute("to", to)
        model.addAttribute("clubNames", clubs.associate { it.id to it.name })
        return "events/list"
    }
    @GetMapping("/login")
    fun showLogin() = "login/login"
}