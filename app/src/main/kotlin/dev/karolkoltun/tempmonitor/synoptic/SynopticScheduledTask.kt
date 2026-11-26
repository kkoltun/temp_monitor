package dev.karolkoltun.tempmonitor.synoptic

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SynopticScheduledTask(val synopticService: SynopticService) {

    companion object {
        private val log = LoggerFactory.getLogger(SynopticScheduledTask::class.java)
    }

    @Scheduled(fixedRateString = "PT15M")
    fun getSynopticData() {
        log.info("Getting synoptic data")
        synopticService.getSynopticData()
    }

}