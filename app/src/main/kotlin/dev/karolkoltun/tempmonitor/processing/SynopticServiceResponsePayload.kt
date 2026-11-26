package dev.karolkoltun.tempmonitor.processing

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class SynopticServiceResponsePayload(
    @SerialName("data_pomiaru") val date: String,
    @SerialName("godzina_pomiaru") val hour: String,
    @SerialName("stacja") val location: String,
    @SerialName("temperatura") val temperature: String,
    @SerialName("wilgotnosc_wzgledna") val humidity: String
)