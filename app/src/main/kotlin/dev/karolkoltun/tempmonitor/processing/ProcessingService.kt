package dev.karolkoltun.tempmonitor.processing

import jakarta.transaction.Transactional
import kotlinx.serialization.json.Json
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class ProcessingService(
    val mqttMessageToProcessRepository: MqttMessageToProcessRepository,
    val deviceLocationRepository: DeviceLocationRepository,
    val readingRepository: ReadingRepository
) {

    @Async
    @Transactional
    fun processMqttMessage(messageId: Long) {
        val message = mqttMessageToProcessRepository.findMessageById(messageId)!!
        println("Found message $message to process")

        val readings = when (message.device) {
            "TS_1" -> mapTsDeviceMessage(message)
            else -> throw IllegalStateException("Unknown device ${message.device}; message: $message")
        }

        val deviceLocation = deviceLocationRepository.findByDevice(message.device)
            ?: throw IllegalStateException("Device ${message.device} location not found")
        val location = deviceLocation.location

        for (reading in readings) {
            when (reading) {
                is Temperature -> readingRepository.save(
                    ReadingEntity(
                        null,
                        messageId,
                        reading.value,
                        reading.readingUnit.toString(),
                        location,
                        message.created,
                        reading.type.toString()
                    )
                )

                is Humidity -> readingRepository.save(
                    ReadingEntity(
                        null,
                        messageId,
                        reading.value,
                        reading.readingUnit,
                        location,
                        message.created,
                        reading.type.toString()
                    )
                )

                is Battery -> readingRepository.save(
                    ReadingEntity(
                        null,
                        messageId,
                        reading.value.toDouble(),
                        reading.readingUnit,
                        location,
                        message.created,
                        reading.type.toString()
                    )
                )

                is LinkQuality -> readingRepository.save(
                    ReadingEntity(
                        null,
                        messageId,
                        reading.value.toDouble(),
                        "",
                        location,
                        message.created,
                        reading.type.toString()
                    )
                )
            }
        }
    }

    private fun mapTsDeviceMessage(messageToProcess: MqttMessageToProcess): List<Reading> {
        val message = Json.decodeFromString(TsDeviceMessagePayload.serializer(), messageToProcess.payload)

        println("Parsed message $message")

        val temperature = Temperature(
            message.temperature,
            Temperature.ReadingUnit.valueOf(message.temperatureUnits.uppercase())
        )
        val humidity = Humidity(message.humidity)
        val battery = Battery(message.battery)
        val linkQuality = LinkQuality(message.linkQuality)

        return listOf(temperature, humidity, battery, linkQuality)
    }

}