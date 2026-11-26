package dev.karolkoltun.tempmonitor.processing

import dev.karolkoltun.tempmonitor.common.MqttMessageEntity
import dev.karolkoltun.tempmonitor.common.SynopticResponseEntity
import org.springframework.data.repository.Repository

interface SynopticServiceResponseToProcessRepository : Repository<SynopticResponseEntity, Long> {

    fun findResponseById(id: Long): SynopticServiceResponseToProcess?

}