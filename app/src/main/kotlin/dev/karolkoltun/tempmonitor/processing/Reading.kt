package dev.karolkoltun.tempmonitor.processing

sealed interface Reading {
    val value: Double
    val readingUnit: ReadingUnit
    val readingType: ReadingType
}

enum class ReadingUnit {
    CELSIUS,
    PERCENT,
    NONE
}

enum class ReadingType {
    TEMPERATURE,
    HUMIDITY,
    BATTERY,
    LINK_QUALITY
}

data class Temperature(
    override val value: Double
) : Reading {
    override val readingUnit: ReadingUnit = ReadingUnit.CELSIUS
    override val readingType: ReadingType = ReadingType.TEMPERATURE
}

data class Humidity(
    override val value: Double
) : Reading {
    override val readingUnit: ReadingUnit = ReadingUnit.PERCENT
    override val readingType: ReadingType = ReadingType.HUMIDITY
}

data class Battery(
    override val value: Double
) : Reading {
    override val readingUnit: ReadingUnit = ReadingUnit.PERCENT
    override val readingType: ReadingType = ReadingType.BATTERY
}

data class LinkQuality(
    override val value: Double,
) : Reading {
    override val readingUnit: ReadingUnit = ReadingUnit.NONE
    override val readingType: ReadingType = ReadingType.LINK_QUALITY
}