package dev.karolkoltun.tempmonitor.processing

import dev.karolkoltun.tempmonitor.processing.ReadingSource.MQTT_MESSAGE
import dev.karolkoltun.tempmonitor.processing.ReadingSource.SYNOPTIC_SERVICE_RESPONSE
import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Service
class ProcessingService(
    private val mqttMessageToProcessRepository: MqttMessageToProcessRepository,
    private val synopticServiceResponseToProcessRepository: SynopticServiceResponseToProcessRepository,
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
            ?: throw IllegalArgumentException("MQTT message to process with id [$messageId] not found")

        log.info("Found MQTT message $message to process")

        val readings = when (message.device) {
            "TS_1" -> mapTsDeviceMqttMessage(message)
            else -> throw IllegalStateException("Unknown device ${message.device}; MQTT message: $message")
        }

        val location = deviceLocationRepository.findByDevice(message.device)?.location
            ?: throw IllegalStateException("MQTT device ${message.device} location not found")

        log.info("Saving ${readings.size} readings from MQTT message $messageId: $readings")

        readingRepository.saveAll(readings.map { it.toEntity(location, message) })

        log.info("Finished processing MQTT message $messageId")
    }

    @Async
    @Transactional
    fun processSynopticServiceResponse(responseId: Long) {
        val response = synopticServiceResponseToProcessRepository.findResponseById(responseId)
            ?: throw IllegalArgumentException("Synoptic service response to process with id [$responseId] not found")

        log.info("Found synoptic service response $response to process")

        val (metadata, readings) = mapSynopticServiceResponse(response)

        log.info("Saving ${readings.size} readings from synoptic service response $response: $readings")

        readingRepository.saveAll(readings.map { it.toEntity(responseId, metadata) })

        log.info("Finished processing synoptic service response $responseId")
    }

    private fun mapTsDeviceMqttMessage(messageToProcess: MqttMessageToProcess): List<Reading> {
        val message = Json.decodeFromString<TsDeviceMessagePayload>(messageToProcess.payload)

        log.info("Parsed MQTT message $message")

        return listOf(
            Temperature(message.temperature),
            Humidity(message.humidity),
            Battery(message.battery.toDouble()),
            LinkQuality(message.linkQuality.toDouble())
        )
    }

    private fun mapSynopticServiceResponse(synopticServiceResponseToProcess: SynopticServiceResponseToProcess): Pair<SynopticMetadata, List<Reading>> {
        val payload = Json.decodeFromString<SynopticServiceResponsePayload>(synopticServiceResponseToProcess.payload)

        log.info("Parsed synoptic service response payload $payload")

        val stamp = LocalDate.parse(payload.date)
            .atTime(Integer.valueOf(payload.hour), 0)
            .atZone(ZoneOffset.UTC)
            .toInstant()

        return Pair(
            SynopticMetadata(stamp, payload.location),
            listOf(
                Temperature(payload.temperature.toDouble()),
                Humidity(payload.humidity.toDouble()),
            )
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
        type = readingType.name,
        source = MQTT_MESSAGE
    )

    private fun Reading.toEntity(
        synopticServiceResponseId: Long,
        synopticMetadata: SynopticMetadata
    ): ReadingEntity = ReadingEntity(
        sourceMessageId = synopticServiceResponseId,
        value = value,
        unit = readingUnit.name,
        location = synopticMetadata.location,
        stamp = synopticMetadata.stamp,
        type = readingType.name,
        source = SYNOPTIC_SERVICE_RESPONSE
    )

    data class SynopticMetadata(val stamp: Instant, val location: String)
}
