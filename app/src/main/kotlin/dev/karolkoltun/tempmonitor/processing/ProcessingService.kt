package dev.karolkoltun.tempmonitor.processing

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ProcessingService(val mqttMessageToProcessRepository: MqttMessageToProcessRepository) {

    @Async
    fun processMqttMessage(messageId: Long) {
        println("Process message $messageId")
        val message = mqttMessageToProcessRepository.findMessageById(messageId)
        println("Message $message on thread ${Thread.currentThread().name}")
    }

}