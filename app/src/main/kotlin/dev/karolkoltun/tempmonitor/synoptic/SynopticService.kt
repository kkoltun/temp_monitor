package dev.karolkoltun.tempmonitor.synoptic

import dev.karolkoltun.tempmonitor.common.SynopticResponseEntity
import dev.karolkoltun.tempmonitor.processing.ProcessingService
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class SynopticService(
    private val processingService: ProcessingService,
    private val synopticRepository: SynopticRepository,
    private val synopticRestClient: SynopticRestClient,
    private val clock: Clock
) {

    fun getSynopticData() {
        val responsePayload = synopticRestClient.get()
        val saved = synopticRepository.save(
            SynopticResponseEntity(
                payload = responsePayload,
                created = Instant.now(clock)
            )
        )

        processingService.processSynopticServiceResponse(saved.id!!)
    }

}