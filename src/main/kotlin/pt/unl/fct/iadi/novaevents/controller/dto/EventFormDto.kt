package pt.unl.fct.iadi.novaevents.controller.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

import java.time.LocalDate

class EventFormDto(
    @field:NotBlank(message = "Name is required")
    var name: String = "",

    @field:NotNull()
    var date: LocalDate? = null,

    @field:NotNull()
    var type: String? = null,

    var location: String = "",
    var description: String = ""
)