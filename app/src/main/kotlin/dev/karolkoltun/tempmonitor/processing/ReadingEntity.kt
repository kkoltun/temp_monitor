package dev.karolkoltun.tempmonitor.processing

import jakarta.persistence.*
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
    var type: String
)