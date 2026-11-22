package dev.karolkoltun.tempmonitor

import dev.karolkoltun.tempmonitor.processing.ProcessingService
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class MqttMessageService(
    val clock: Clock,
    val mqttProperties: MqttProperties,
    val repository: MqttMessageRepository,
    val processingService: ProcessingService
) : MessageHandler {

    override fun handleMessage(message: Message<*>) {
        println("Handling message $message")

        val topicName = message.headers.get("mqtt_receivedTopic", String::class.java)!!

        val mqttMessageEntity = MqttMessageEntity(
            id = null,
            topic = topicName,
            headers = message.headers.toString(),
            payload = message.payload.toString(),
            device = parseDevice(topicName),
            created = Instant.now(clock),
        )

        repository.save(mqttMessageEntity)

        processingService.processMqttMessage(mqttMessageEntity.id!!)
    }

    private fun parseDevice(topic: String): String {
        if (!topic.startsWith(mqttProperties.topicPrefix)) {
            throw IllegalArgumentException("Topic $topic has unexpected structure - no ${mqttProperties.topicPrefix}")
        }

        return topic.removePrefix(mqttProperties.topicPrefix);
    }

}