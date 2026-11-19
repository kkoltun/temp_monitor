package dev.karolkoltun.tempmonitor

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter

@SpringBootApplication
class TempMonitorApplication {
    @Bean
    fun mqttInbound(): IntegrationFlow {
        val options = MqttConnectOptions()
        options.userName = "app"
        options.password = System.getenv("password").toCharArray()

        val factory = DefaultMqttPahoClientFactory()
        factory.connectionOptions = options

        val channelAdapter = MqttPahoMessageDrivenChannelAdapter(
            "tcp://localhost:1883",
            "client",
            factory,
            "zigbee2mqtt/+"
        )

        return IntegrationFlow.from(channelAdapter)
            .handle { p -> println(p) }
            .get()
    }
}

fun main(args: Array<String>) {
    runApplication<TempMonitorApplication>(*args)
}