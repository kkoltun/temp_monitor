package dev.karolkoltun.tempmonitor

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.mqtt")
data class MqttCredentials(
    val userName: String,
    val password: String
)
