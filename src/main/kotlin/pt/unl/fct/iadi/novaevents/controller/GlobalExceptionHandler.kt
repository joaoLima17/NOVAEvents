package pt.unl.fct.iadi.novaevents.controller


import org.springframework.http.HttpStatus
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.ResponseStatus
import pt.unl.fct.iadi.novaevents.model.EventType

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: NoSuchElementException, model: ModelMap  ): String {
        model.addAttribute("message", ex.message ?: "Not found")
        return "error/404"
    }

    @ModelAttribute("eventTypes")
    fun eventTypes() = EventType.entries.toTypedArray()
}