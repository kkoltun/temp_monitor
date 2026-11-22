package dev.karolkoltun.tempmonitor

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter

@Configuration
class MqttIntegrationFlowConfig {
    @Bean
    fun mqttInbound(
        mqttProperties: MqttProperties,
        mqttCredentials: MqttCredentials,
        mqttMessageService: MqttMessageService
    ): IntegrationFlow {
        val options = MqttConnectOptions()
        options.userName = mqttCredentials.userName
        options.password = mqttCredentials.password.toCharArray()

        val factory = DefaultMqttPahoClientFactory()
        factory.connectionOptions = options

        val channelAdapter = MqttPahoMessageDrivenChannelAdapter(
            mqttProperties.url,
            mqttProperties.clientId,
            factory,
            "${mqttProperties.topicPrefix}+"
        )

        return IntegrationFlow.from(channelAdapter)
            .handle(mqttMessageService)
            .get()
    }
}