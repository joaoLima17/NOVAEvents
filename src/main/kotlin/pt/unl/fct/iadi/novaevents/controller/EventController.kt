package pt.unl.fct.iadi.novaevents.controller

import jakarta.validation.Valid
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.EventService
import java.time.LocalDate

@Controller
@RequestMapping("/clubs/{clubId}/events")
class EventController (private val eventService: EventService, private val clubService: ClubService ){

    @GetMapping
    fun listEvents(
        @RequestParam(required = false) type: EventType?,
        @PathVariable @RequestParam(required = false) clubId: Long?,
        @RequestParam(required = false) from: LocalDate?,
        @RequestParam(required = false) to: LocalDate?,
        model: ModelMap
    ): String {
        model.addAttribute("events", eventService.findAll(type, clubId, from, to))
        model.addAttribute("clubs", clubService.findAll())
        model.addAttribute("eventTypes", EventType.entries.toTypedArray())
        return "events/list"
    }
    @GetMapping("/{eventId}")
    fun eventDetail(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("event", eventService.findById(eventId))
        return "events/detail"
    }

    @GetMapping("/new")
    fun createForm(@PathVariable clubId: Long, model: Model): String {
        clubService.findById(clubId) // 404 if not found
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("form", EventFormDto())
        model.addAttribute("eventTypes", EventType.values())
        return "events/form"
    }
    @PostMapping
    fun createEvent(
        @PathVariable clubId: Long,
        @Valid @ModelAttribute("form") form: EventFormDto,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (eventService.existsByName(form.name)) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("club", clubService.findById(clubId))
            model.addAttribute("eventTypes", EventType.values())
            return "events/form"
        }
        val event = eventService.create(clubId, form)
        return "redirect:/clubs/${clubId}/events/${event.id}"
    }

    @GetMapping("/{eventId}/edit")
    fun editForm(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        val event = eventService.findById(eventId)
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("event", event)
        model.addAttribute("form", EventFormDto(
            name = event.name,
            date = event.date,
            type = event.type,
            location = event.location ?: "",
            description = event.description ?: ""
        ))
        model.addAttribute("eventTypes", EventType.values())
        return "events/form"
    }

    @PutMapping("/{eventId}")
    fun updateEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        @Valid @ModelAttribute("form") form: EventFormDto,
        bindingResult: BindingResult,
        model: Model
    ): String {

        if (eventService.existsByNameExcluding(form.name, eventId)) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("club", clubService.findById(clubId))
            model.addAttribute("event", eventService.findById(eventId))
            model.addAttribute("eventTypes", EventType.values())
            return "events/form"
        }
        eventService.update(eventId, form)
        return "redirect:/clubs/${clubId}/events/${eventId}"
    }

    @GetMapping("/{eventId}/delete")
    fun deleteConfirm(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("event", eventService.findById(eventId))
        return "events/delete"
    }

    @DeleteMapping("/{eventId}")
    fun deleteEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
    ): String {
        eventService.delete(eventId)
        return "redirect:/clubs/${clubId}"
    }
}