package dev.karolkoltun.tempmonitor.processing

import dev.karolkoltun.tempmonitor.MqttMessageEntity
import org.springframework.data.repository.Repository

interface MqttMessageToProcessRepository : Repository<MqttMessageEntity, Long> {

    fun findMessageById(id: Long): MqttMessageToProcess?

}