package dev.karolkoltun.tempmonitor.mqtt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.mqtt")
data class MqttProperties(
    val url: String,
    val clientId: String,
    val topicPrefix: String
)
