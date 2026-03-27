package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "event_type")
class EventType (
    WORKSHOP, TALK, COMPETITION, SOCIAL, MEETING, OTHER
)