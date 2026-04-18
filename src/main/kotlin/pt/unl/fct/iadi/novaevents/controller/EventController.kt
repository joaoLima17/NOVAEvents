package pt.unl.fct.iadi.novaevents.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.EventService

@Controller
@RequestMapping("/clubs/{clubId}/events")
class EventController (private val eventService: EventService, private val clubService: ClubService){


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
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("form", EventFormDto())
        model.addAttribute("eventTypes", eventService.findAllEventTypes())
        return "events/form"
    }


    @PostMapping
    fun createEvent(
        @PathVariable clubId: Long,
        @Valid @ModelAttribute("form") form: EventFormDto,
        bindingResult: BindingResult,
        model: Model,
        authentication: Authentication
    ): String {
        if (eventService.existsByName(form.name)) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("club", clubService.findById(clubId))
            model.addAttribute("eventTypes", eventService.findAllEventTypes())
            return "events/form"
        }
        val event = eventService.create(clubId, form, authentication.name)
        return "redirect:/clubs/${clubId}/events/${event.id}"
    }

    @GetMapping("/{eventId}/edit")
    @PreAuthorize("@eventOwnershipService.isOwner(#eventId, authentication.name)")
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
            type = event.type.name,
            location = event.location ?: "",
            description = event.description ?: ""
        ))
        model.addAttribute("eventTypes", eventService.findAllEventTypes())
        return "events/form"
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("@eventOwnershipService.isOwner(#eventId, authentication.name)")
    fun updateEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        @Valid @ModelAttribute("form") form: EventFormDto,
        bindingResult: BindingResult,
        model: Model
    ): String {
        if (!bindingResult.hasFieldErrors("name") && eventService.existsByNameExcluding(form.name, eventId)) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("club", clubService.findById(clubId))
            model.addAttribute("event", eventService.findById(eventId))
            model.addAttribute("eventTypes", eventService.findAllEventTypes())
            return "events/form"
        }
        eventService.update(eventId, form)
        return "redirect:/clubs/${clubId}/events/${eventId}"
    }

    @GetMapping("/{eventId}/delete")
    @PreAuthorize("hasRole('ADMIN') or @eventOwnershipService.isOwner(#eventId, authentication.name)")
    fun deleteConfirm(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("event", eventService.findById(eventId))
        return "events/confirm-delete"
    }
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or @eventOwnershipService.isOwner(#eventId, authentication.name)")
    fun deleteEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
    ): String {
        eventService.delete(eventId)
        return "redirect:/clubs/${clubId}"
    }
}