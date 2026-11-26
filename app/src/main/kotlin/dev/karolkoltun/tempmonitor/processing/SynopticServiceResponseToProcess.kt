package dev.karolkoltun.tempmonitor.processing

import java.time.Instant

data class SynopticServiceResponseToProcess(
    val id: Long,
    val payload: String,
    val created: Instant,
)
