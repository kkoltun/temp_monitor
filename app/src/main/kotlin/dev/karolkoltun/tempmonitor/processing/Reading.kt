package dev.karolkoltun.tempmonitor.processing

sealed interface Reading

enum class ReadingType {
    TEMPERATURE,
    HUMIDITY,
    BATTERY,
    LINK_QUALITY
}

data class Temperature(
    val value: Double,
    val readingUnit: ReadingUnit,
    val type: ReadingType = ReadingType.TEMPERATURE
) : Reading {

    enum class ReadingUnit {
        FAHRENHEIT,
        CELSIUS
    }

}

data class Humidity(
    val value: Double,
    val readingUnit: String = "PERCENT",
    val type: ReadingType = ReadingType.HUMIDITY
) : Reading

data class Battery(
    val value: Int,
    val readingUnit: String = "PERCENT",
    val type: ReadingType = ReadingType.BATTERY
) : Reading

data class LinkQuality(
    val value: Int,
    val type: ReadingType = ReadingType.LINK_QUALITY
) : Reading