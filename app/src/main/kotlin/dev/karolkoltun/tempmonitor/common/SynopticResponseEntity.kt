package dev.karolkoltun.tempmonitor.common

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "synoptic_service_response")
class SynopticResponseEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,
    var payload: String,
    var created: Instant
)