package dev.karolkoltun.tempmonitor.synoptic

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app.synoptic")
class SynopticProperties(
    val stationId: String
)