package dev.karolkoltun.tempmonitor.mqtt

import dev.karolkoltun.tempmonitor.common.MqttMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MqttMessageRepository : JpaRepository<MqttMessageEntity, Long>