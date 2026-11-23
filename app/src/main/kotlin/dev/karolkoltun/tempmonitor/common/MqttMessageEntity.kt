package dev.karolkoltun.tempmonitor.common

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "mqtt_message")
class MqttMessageEntity(
    @Id @GeneratedValue(strategy = IDENTITY) var id: Long? = null,
    var topic: String,
    var device: String,
    var headers: String,
    var payload: String,
    var created: Instant,
)