package dev.karolkoltun.tempmonitor.processing

import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ProcessingService(
    private val mqttMessageToProcessRepository: MqttMessageToProcessRepository,
    private val deviceLocationRepository: DeviceLocationRepository,
    private val readingRepository: ReadingRepository
) {

    companion object {
        private val log = LoggerFactory.getLogger(ProcessingService::class.java)
    }

    @Async
    @Transactional
    fun processMqttMessage(messageId: Long) {
        val message = mqttMessageToProcessRepository.findMessageById(messageId)
            ?: throw IllegalArgumentException("Message to process with id [$messageId] not found")

        log.info("Found message $message to process")

        val readings = when (message.device) {
            "TS_1" -> mapTsDeviceMessage(message)
            else -> throw IllegalStateException("Unknown device ${message.device}; message: $message")
        }

        val location = deviceLocationRepository.findByDevice(message.device)?.location
            ?: throw IllegalStateException("Device ${message.device} location not found")

        log.info("Saving ${readings.size} readings: $readings")

        readingRepository.saveAll(readings.map { it.toEntity(location, message) })

        log.info("Finished processing message $messageId")
    }

    private fun mapTsDeviceMessage(messageToProcess: MqttMessageToProcess): List<Reading> {
        val message = Json.decodeFromString<TsDeviceMessagePayload>(messageToProcess.payload)

        log.info("Parsed message $message")

        return listOf(
            Temperature(message.temperature),
            Humidity(message.humidity),
            Battery(message.battery.toDouble()),
            LinkQuality(message.linkQuality.toDouble())
        )
    }

    private fun Reading.toEntity(
        location: String,
        message: MqttMessageToProcess
    ): ReadingEntity = ReadingEntity(
        sourceMessageId = message.id,
        value = value,
        unit = readingUnit.name,
        location = location,
        stamp = message.created,
        type = readingType.name
    )

}