package dev.karolkoltun.tempmonitor.processing

import jakarta.persistence.*
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.GenerationType.IDENTITY
import java.time.Instant

@Entity
@Table(name = "reading")
class ReadingEntity(
    @Id @GeneratedValue(strategy = IDENTITY) var id: Long? = null,
    @Column(name = "source_message_id") var sourceMessageId: Long,
    var value: Double,
    var unit: String,
    var location: String,
    var stamp: Instant,
    var type: String,
    @Enumerated(STRING)
    var source: ReadingSource
)

enum class ReadingSource {
    MQTT_MESSAGE,
    SYNOPTIC_SERVICE_RESPONSE
}