package dev.karolkoltun.tempmonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
@EnableConfigurationProperties(MqttProperties::class, MqttCredentials::class)
class TempMonitorApplication {
}

fun main(args: Array<String>) {
    runApplication<TempMonitorApplication>(*args)
}