package pt.unl.fct.iadi.novaevents.controller

import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
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
import pt.unl.fct.iadi.novaevents.model.appUser
import pt.unl.fct.iadi.novaevents.repository.AppUserDetailsManager
import pt.unl.fct.iadi.novaevents.service.ClubService
import pt.unl.fct.iadi.novaevents.service.EventService
import pt.unl.fct.iadi.novaevents.service.WeatherService

private const val HIKING_CLUB_NAME = "Hiking & Outdoors Club"
@Controller
@RequestMapping("/clubs/{clubId}/events")
class EventController (private val eventService: EventService, private val clubService: ClubService, private val weatherService: WeatherService, private val Useranager: AppUserDetailsManager) {


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

    @PreAuthorize("@eventService.isOwner(#eventId, authentication.name) or hasRole('ADMIN')")
    @PostMapping
    fun createEvent(
        @PathVariable clubId: Long,
        @Valid @ModelAttribute("form") form: EventFormDto,
        user: appUser,
        bindingResult: BindingResult,
        model: Model
    ): String {
        val club = clubService.findById(clubId)
        if (eventService.existsByName(form.name)) {
            bindingResult.rejectValue("name", "duplicate", "An event with this name already exists")
        }
        // Outdoor club location validation
        if (club.name == HIKING_CLUB_NAME && form.location.isBlank()) {
            bindingResult.rejectValue("location", "required", "Location is required for outdoor events")
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("club", clubService.findById(clubId))
            model.addAttribute("eventTypes", eventService.findAllEventTypes())
            return "events/form"
        }

        // Weather check for outdoor club (only if location is provided)
        if (club.name == HIKING_CLUB_NAME && form.location.isNotBlank()) {
            val raining = weatherService.isRaining(form.location)
            if (raining == true) {
                bindingResult.rejectValue(
                    "location", "weather",
                    "It is currently raining at \"${form.location}\" — outdoor events cannot be created in bad weather"
                )
                model.addAttribute("club", club)
                model.addAttribute("eventTypes", eventService.findAllEventTypes())
                return "events/form"
            }
        }
        val event = eventService.create(clubId, form, user)
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
            type = event.type.name,
            location = event.location ?: "",
            description = event.description ?: ""
        ))
        model.addAttribute("eventTypes", eventService.findAllEventTypes())
        return "events/form"
    }

    @PreAuthorize("@eventService.findById(#eventId).owner.username == authentication.name")
    @PutMapping("/{eventId}")
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
    fun deleteConfirm(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
        model: Model
    ): String {
        model.addAttribute("club", clubService.findById(clubId))
        model.addAttribute("event", eventService.findById(eventId))
        return "events/confirm-delete"
    }

    @PreAuthorize("@eventService.isOwner(#eventId, authentication.name) or hasRole('ADMIN')")
    @DeleteMapping("/{eventId}")
    fun deleteEvent(
        @PathVariable clubId: Long,
        @PathVariable eventId: Long,
    ): String {
        eventService.delete(eventId)
        return "redirect:/clubs/${clubId}"
    }
}