package dev.karolkoltun.tempmonitor.processing

import java.time.Instant

data class MqttMessageToProcess(
    val id: Long,
    val topic: String,
    val device: String,
    val headers: String,
    val payload: String,
    val created: Instant,
)
