package dev.karolkoltun.tempmonitor.processing

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class TsDeviceMessagePayload(
    val battery: Int,
    val humidity: Double,
    val temperature: Double,
    @SerialName("linkquality") val linkQuality: Int
)
