package dev.karolkoltun.tempmonitor.mqtt

import dev.karolkoltun.tempmonitor.common.MqttMessageEntity
import dev.karolkoltun.tempmonitor.processing.ProcessingService
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message
import org.springframework.messaging.MessageHandler
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class MqttMessageService(
    private val clock: Clock,
    private val mqttProperties: MqttProperties,
    private val repository: MqttMessageRepository,
    private val processingService: ProcessingService
) : MessageHandler {

    companion object {
        private val log = LoggerFactory.getLogger(MqttMessageService::class.java)
    }

    override fun handleMessage(message: Message<*>) {
        log.info("Handling message $message")

        val topicName = message.headers.get("mqtt_receivedTopic", String::class.java)
            ?: throw IllegalArgumentException("Topic not found in headers: ${message.headers}")

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

        log.info("Finished handling message; assigned id ${mqttMessageEntity.id}")
    }

    private fun parseDevice(topic: String): String {
        require(topic.startsWith(mqttProperties.topicPrefix)) { "Topic $topic has unexpected structure - no ${mqttProperties.topicPrefix}" }

        return topic.removePrefix(mqttProperties.topicPrefix)
    }

}