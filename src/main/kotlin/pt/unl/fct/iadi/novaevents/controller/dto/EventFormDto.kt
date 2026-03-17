package pt.unl.fct.iadi.novaevents.controller.dto

import jakarta.validation.constraints.NotBlank
import org.jetbrains.annotations.NotNull
import pt.unl.fct.iadi.novaevents.model.EventType
import java.time.LocalDate

class EventFormDto(
    @field:NotBlank(message = "Name is required")
    val name: String = "",

    @field:NotNull()
    val date: LocalDate? = null,

    @field:NotNull()
    val type: EventType? = null,

    val location: String = "",
    val description: String = ""
){
}