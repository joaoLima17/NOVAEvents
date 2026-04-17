package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import pt.unl.fct.iadi.novaevents.model.Event
import java.time.LocalDate


@Repository
interface EventRepository : JpaRepository<Event, Long> {

    fun existsByNameIgnoreCase(name: String): Boolean

    fun existsByNameIgnoreCaseAndIdNot(name: String, id: Long): Boolean

    fun findByClubId(clubId: Long): List<Event>

    fun countByClubId(clubId: Long): Long

    fun existsByIdAndOwnerUsername(id: Long, username: String): Boolean

    fun findByOwnerIsNull(): List<Event>

    @Query("""
        SELECT e FROM Event e
        JOIN e.type t
        WHERE (:typeName IS NULL OR t.name = :typeName)
          AND (:clubId   IS NULL OR e.clubId = :clubId)
          AND (:from     IS NULL OR e.date >= :from)
          AND (:to       IS NULL OR e.date <= :to)
        ORDER BY e.date ASC
    """)
    fun findFiltered(
        @Param("typeName") typeName: String?,
        @Param("clubId")   clubId: Long?,
        @Param("from")     from: LocalDate?,
        @Param("to")       to: LocalDate?
    ): List<Event>
}
