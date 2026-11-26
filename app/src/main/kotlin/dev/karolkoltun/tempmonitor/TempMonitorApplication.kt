package dev.karolkoltun.tempmonitor

import dev.karolkoltun.tempmonitor.mqtt.MqttCredentials
import dev.karolkoltun.tempmonitor.mqtt.MqttProperties
import dev.karolkoltun.tempmonitor.synoptic.SynopticProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(MqttProperties::class, MqttCredentials::class, SynopticProperties::class)
class TempMonitorApplication

fun main(args: Array<String>) {
    runApplication<TempMonitorApplication>(*args)
}